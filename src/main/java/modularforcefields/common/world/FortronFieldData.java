package modularforcefields.common.world;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.registers.ModularForcefieldsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public final class FortronFieldData extends SavedData {

    private static final String DATA_NAME = "fortron_fields";

    private long nextProjectorId = 1;

    /*
     * Persistent:
     *
     * projector ID -> projector + all positions it currently owns
     */
    private final Long2ObjectOpenHashMap<ProjectorData> projectors = new Long2ObjectOpenHashMap<>();

    /*
     * Persistent:
     *
     * Fields that have zero owners but whose physical block has not yet been
     * removed. Grouped by chunk so unloaded chunks are trivial to deal with.
     */
    private final Long2ObjectOpenHashMap<LongOpenHashSet> pendingRemovals = new Long2ObjectOpenHashMap<>();

    /*
     * Runtime only:
     *
     * chunk -> field position -> projector IDs
     *
     * This can always be rebuilt from "projectors", so there is no reason to save
     * it twice.
     */
    private final Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<LongOpenHashSet>> ownersByChunk = new Long2ObjectOpenHashMap<>();

    /*
     * Runtime cleanup queue.
     */
    private final LongArrayList cleanupPositions = new LongArrayList();
    private final LongOpenHashSet queuedPositions = new LongOpenHashSet();

    public static FortronFieldData get(ServerLevel level) {

	DimensionDataStorage storage = level.getDataStorage();

	return storage.computeIfAbsent(FortronFieldData::load, FortronFieldData::new, DATA_NAME);
    }

    /**
     * Registers a projector and returns its permanent ID.
     *
     * Zero means "this projector has never received an ID".
     */
    public long registerProjector(long requestedId, BlockPos projectorPos) {

	long packedPos = projectorPos.asLong();

	if (requestedId > 0) {

	    ProjectorData existing = projectors.get(requestedId);

	    if (existing == null) {

		projectors.put(requestedId, new ProjectorData(packedPos));

		nextProjectorId = Math.max(nextProjectorId, requestedId + 1);

		setDirty();

		return requestedId;
	    }

	    /*
	     * Same projector loading normally.
	     */
	    if (existing.projectorPos == packedPos) {
		return requestedId;
	    }

	    /*
	     * Same ID occurring at a different position means something copied the
	     * projector NBT. Give this projector a new identity.
	     */
	}

	long id = nextProjectorId++;

	projectors.put(id, new ProjectorData(packedPos));

	setDirty();

	return id;
    }

    /**
     * Claims a physical forcefield position for a projector.
     */
    public void claim(long projectorId, BlockPos projectorPos, BlockPos fieldPos) {

	if (projectorId <= 0) {
	    return;
	}

	ProjectorData projector = projectors.get(projectorId);

	if (projector == null) {

	    projector = new ProjectorData(projectorPos.asLong());

	    projectors.put(projectorId, projector);

	    nextProjectorId = Math.max(nextProjectorId, projectorId + 1);
	}

	long chunkKey = new ChunkPos(fieldPos).toLong();
	long packedPos = fieldPos.asLong();

	LongOpenHashSet projectorFields = projector.fieldsByChunk.computeIfAbsent(chunkKey,
		key -> new LongOpenHashSet());

	boolean changed = projectorFields.add(packedPos);

	Long2ObjectOpenHashMap<LongOpenHashSet> chunkOwners = ownersByChunk.computeIfAbsent(chunkKey,
		key -> new Long2ObjectOpenHashMap<>());

	LongOpenHashSet owners = chunkOwners.computeIfAbsent(packedPos, key -> new LongOpenHashSet());

	changed |= owners.add(projectorId);

	/*
	 * A field may have been scheduled for deletion and then become useful again
	 * before cleanup reached it.
	 */
	LongOpenHashSet pending = pendingRemovals.get(chunkKey);

	if (pending != null && pending.remove(packedPos)) {

	    changed = true;

	    if (pending.isEmpty()) {
		pendingRemovals.remove(chunkKey);
	    }
	}

	if (changed) {
	    setDirty();
	}
    }

    /**
     * Releases every field currently owned by this projector.
     *
     * Ownership disappears immediately. Physical blocks can disappear gradually.
     */
    public void releaseAllFields(ServerLevel level, long projectorId) {

	ProjectorData projector = projectors.get(projectorId);

	if (projector == null || projector.fieldsByChunk.isEmpty()) {
	    return;
	}

	for (Long2ObjectMap.Entry<LongOpenHashSet> chunkEntry : projector.fieldsByChunk.long2ObjectEntrySet()) {

	    long chunkKey = chunkEntry.getLongKey();

	    /*
	     * Copy because the ownership structures change during release.
	     */
	    long[] positions = chunkEntry.getValue().toLongArray();

	    for (long packedPos : positions) {
		releaseOwnership(level, projectorId, chunkKey, packedPos);
	    }
	}

	projector.fieldsByChunk.clear();

	setDirty();
    }

    /**
     * Permanently removes a projector from the manager.
     */
    public void removeProjector(ServerLevel level, long projectorId) {

	releaseAllFields(level, projectorId);

	if (projectors.remove(projectorId) != null) {
	    setDirty();
	}
    }

    private void releaseOwnership(ServerLevel level, long projectorId, long chunkKey, long packedPos) {
	Long2ObjectOpenHashMap<LongOpenHashSet> chunkOwners = ownersByChunk.get(chunkKey);

	boolean hasRemainingOwners = false;

	if (chunkOwners != null) {

	    LongOpenHashSet owners = chunkOwners.get(packedPos);

	    if (owners != null) {

		owners.remove(projectorId);

		hasRemainingOwners = !owners.isEmpty();

		if (!hasRemainingOwners) {
		    chunkOwners.remove(packedPos);
		}
	    }

	    if (chunkOwners.isEmpty()) {
		ownersByChunk.remove(chunkKey);
	    }
	}

	/*
	 * Another projector still owns this physical block.
	 */
	if (hasRemainingOwners) {
	    return;
	}

	BlockPos pos = BlockPos.of(packedPos);

	LongOpenHashSet pending = pendingRemovals.computeIfAbsent(chunkKey, key -> new LongOpenHashSet());

	pending.add(packedPos);

	if (level.hasChunkAt(pos)) {
	    queuePosition(packedPos);
	}
    }

    private void queuePosition(long packedPos) {
	if (queuedPositions.add(packedPos)) {
	    cleanupPositions.add(packedPos);
	}
    }

    public boolean hasFields(long projectorId) {
	ProjectorData projector = projectors.get(projectorId);
	return projector != null && !projector.fieldsByChunk.isEmpty();
    }

    public int getFieldCount(long projectorId) {
	ProjectorData projector = projectors.get(projectorId);
	if (projector == null) {
	    return 0;
	}
	int count = 0;
	for (LongOpenHashSet positions : projector.fieldsByChunk.values()) {
	    count += positions.size();
	}
	return count;
    }

    public boolean hasOwners(BlockPos pos) {
	long chunkKey = new ChunkPos(pos).toLong();
	Long2ObjectOpenHashMap<LongOpenHashSet> chunkOwners = ownersByChunk.get(chunkKey);
	if (chunkOwners == null) {
	    return false;
	}
	LongOpenHashSet owners = chunkOwners.get(pos.asLong());
	return owners != null && !owners.isEmpty();
    }

    /**
     * Returns a copy so callers cannot mutate manager ownership.
     */
    public long[] getOwners(BlockPos pos) {

	long chunkKey = new ChunkPos(pos).toLong();

	Long2ObjectOpenHashMap<LongOpenHashSet> chunkOwners = ownersByChunk.get(chunkKey);

	if (chunkOwners == null) {
	    return new long[0];
	}

	LongOpenHashSet owners = chunkOwners.get(pos.asLong());

	return owners == null ? new long[0] : owners.toLongArray();
    }

    public @Nullable TileFortronFieldProjector getLoadedProjector(ServerLevel level, long projectorId) {

	ProjectorData projector = projectors.get(projectorId);

	if (projector == null) {
	    return null;
	}

	BlockPos pos = BlockPos.of(projector.projectorPos);

	/*
	 * Never force-load the owner chunk.
	 */
	if (!level.hasChunkAt(pos)) {
	    return null;
	}

	BlockEntity blockEntity = level.getBlockEntity(pos);

	if (blockEntity instanceof TileFortronFieldProjector fieldProjector
		&& fieldProjector.getProjectorId() == projectorId) {

	    return fieldProjector;
	}

	return null;
    }

    /**
     * Called from ChunkEvent.Load.
     *
     * Do NOT modify the level here; simply enqueue the chunk.
     */
    public void onChunkLoad(ChunkPos chunkPos) {

	LongOpenHashSet pending = pendingRemovals.get(chunkPos.toLong());

	if (pending == null) {
	    return;
	}

	for (long packedPos : pending) {

	    if (queuedPositions.add(packedPos)) {
		cleanupPositions.add(packedPos);
	    }
	}
    }

    /**
     * Removes up to {@code budget} orphaned physical field blocks.
     */
    public void tickCleanup(ServerLevel level, int budget) {

	boolean changed = false;

	while (budget-- > 0 && !cleanupPositions.isEmpty()) {

	    int index = level.random.nextInt(cleanupPositions.size());

	    long packedPos = cleanupPositions.getLong(index);

	    // swap-remove: O(1), unlike removing from the middle of an ArrayList
	    int lastIndex = cleanupPositions.size() - 1;
	    long last = cleanupPositions.removeLong(lastIndex);

	    if (index < cleanupPositions.size()) {
		cleanupPositions.set(index, last);
	    }

	    queuedPositions.remove(packedPos);

	    BlockPos pos = BlockPos.of(packedPos);
	    long chunkKey = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);

	    /*
	     * Chunk may have unloaded since the position entered the runtime queue. Leave
	     * it persisted in pendingRemovals.
	     *
	     * When the chunk loads again, onChunkLoad() requeues it.
	     */
	    if (!level.hasChunkAt(pos)) {
		continue;
	    }

	    LongOpenHashSet pending = pendingRemovals.get(chunkKey);

	    if (pending == null || !pending.remove(packedPos)) {
		continue;
	    }

	    /*
	     * Normally claim() already removes the position from pending, but keep this
	     * defensive ownership check.
	     */
	    if (!hasOwners(chunkKey, packedPos)) {
		removePhysicalField(level, pos);
	    }

	    if (pending.isEmpty()) {
		pendingRemovals.remove(chunkKey);
	    }

	    changed = true;
	}

	if (changed) {
	    setDirty();
	}
    }

    private boolean hasOwners(long chunkKey, long packedPos) {

	Long2ObjectOpenHashMap<LongOpenHashSet> chunkOwners = ownersByChunk.get(chunkKey);

	if (chunkOwners == null) {
	    return false;
	}

	LongOpenHashSet owners = chunkOwners.get(packedPos);

	return owners != null && !owners.isEmpty();
    }

    private static void removePhysicalField(ServerLevel level, BlockPos pos) {

	if (level.getBlockState(pos).is(ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get())) {
	    level.removeBlock(pos, false);
	}
    }

    private void rebuildOwners() {

	ownersByChunk.clear();

	for (Long2ObjectMap.Entry<ProjectorData> projectorEntry : projectors.long2ObjectEntrySet()) {

	    long projectorId = projectorEntry.getLongKey();

	    for (Long2ObjectMap.Entry<LongOpenHashSet> chunkEntry : projectorEntry.getValue().fieldsByChunk
		    .long2ObjectEntrySet()) {

		long chunkKey = chunkEntry.getLongKey();

		Long2ObjectOpenHashMap<LongOpenHashSet> chunkOwners = ownersByChunk.computeIfAbsent(chunkKey,
			key -> new Long2ObjectOpenHashMap<>());

		for (long packedPos : chunkEntry.getValue()) {

		    chunkOwners.computeIfAbsent(packedPos, key -> new LongOpenHashSet()).add(projectorId);
		}
	    }
	}
    }

    @Override
    public CompoundTag save(CompoundTag tag) {

	tag.putLong("nextProjectorId", nextProjectorId);

	ListTag projectorList = new ListTag();

	for (Long2ObjectMap.Entry<ProjectorData> projectorEntry : projectors.long2ObjectEntrySet()) {

	    CompoundTag projectorTag = new CompoundTag();

	    projectorTag.putLong("id", projectorEntry.getLongKey());

	    ProjectorData projector = projectorEntry.getValue();

	    projectorTag.putLong("pos", projector.projectorPos);

	    ListTag chunkList = new ListTag();

	    for (Long2ObjectMap.Entry<LongOpenHashSet> chunkEntry : projector.fieldsByChunk.long2ObjectEntrySet()) {

		CompoundTag chunkTag = new CompoundTag();

		chunkTag.putLong("chunk", chunkEntry.getLongKey());
		chunkTag.putLongArray("positions", chunkEntry.getValue().toLongArray());

		chunkList.add(chunkTag);
	    }

	    projectorTag.put("chunks", chunkList);

	    projectorList.add(projectorTag);
	}

	tag.put("projectors", projectorList);

	ListTag pendingList = new ListTag();

	for (Long2ObjectMap.Entry<LongOpenHashSet> entry : pendingRemovals.long2ObjectEntrySet()) {

	    CompoundTag chunkTag = new CompoundTag();

	    chunkTag.putLong("chunk", entry.getLongKey());
	    chunkTag.putLongArray("positions", entry.getValue().toLongArray());

	    pendingList.add(chunkTag);
	}

	tag.put("pendingRemovals", pendingList);

	return tag;
    }

    public static FortronFieldData load(CompoundTag tag) {

	FortronFieldData data = new FortronFieldData();

	data.nextProjectorId = Math.max(1, tag.getLong("nextProjectorId"));

	ListTag projectorList = tag.getList("projectors", Tag.TAG_COMPOUND);

	for (Tag rawProjector : projectorList) {

	    CompoundTag projectorTag = (CompoundTag) rawProjector;

	    long id = projectorTag.getLong("id");

	    ProjectorData projector = new ProjectorData(projectorTag.getLong("pos"));

	    ListTag chunkList = projectorTag.getList("chunks", Tag.TAG_COMPOUND);

	    for (Tag rawChunk : chunkList) {

		CompoundTag chunkTag = (CompoundTag) rawChunk;

		long chunkKey = chunkTag.getLong("chunk");

		LongOpenHashSet positions = new LongOpenHashSet();

		for (long packedPos : chunkTag.getLongArray("positions")) {
		    positions.add(packedPos);
		}

		if (!positions.isEmpty()) {
		    projector.fieldsByChunk.put(chunkKey, positions);
		}
	    }

	    data.projectors.put(id, projector);

	    data.nextProjectorId = Math.max(data.nextProjectorId, id + 1);
	}

	ListTag pendingList = tag.getList("pendingRemovals", Tag.TAG_COMPOUND);

	for (Tag rawChunk : pendingList) {

	    CompoundTag chunkTag = (CompoundTag) rawChunk;

	    LongOpenHashSet positions = new LongOpenHashSet();

	    for (long packedPos : chunkTag.getLongArray("positions")) {
		positions.add(packedPos);
	    }

	    if (!positions.isEmpty()) {
		data.pendingRemovals.put(chunkTag.getLong("chunk"), positions);
	    }
	}

	data.rebuildOwners();

	return data;
    }

    private static final class ProjectorData {

	private final long projectorPos;

	private final Long2ObjectOpenHashMap<LongOpenHashSet> fieldsByChunk = new Long2ObjectOpenHashMap<>();

	private ProjectorData(long projectorPos) {
	    this.projectorPos = projectorPos;
	}
    }
}