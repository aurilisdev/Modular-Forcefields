package modularforcefields.common.tile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.WorldUtils;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronConnective extends GenericTile {
	protected HashSet<TileFortronConnective> connections = new HashSet<>();
	protected int frequency = 0;

	protected TileFortronConnective(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
		super(tileEntityTypeIn, worldPos, blockState);
		addComponent(new ComponentTickable().tickServer(this::tickServer).tickCommon(this::tickCommon));
	}

	protected void tickCommon(ComponentTickable tickable) {
		long ticks = tickable.getTicks();
		if (ticks % 1200 == 1) {
			findConnections();
		}
		if (ticks % 20 == 0) {
			validateConnections();
		}
	}

	protected void findConnections() {
		Predicate<BlockEntity> predicate = getConnectionTest();
		for (BlockEntity entity : WorldUtils.getNearbyTiles(level, worldPosition, 5)) {
			if (entity != this && entity instanceof TileFortronConnective connection && predicate.test(entity)) {
				connections.add(connection);
				connection.connections.add(this);

			}
		}
	}

	protected void validateConnections() {
		Iterator<TileFortronConnective> it = connections.iterator();
		while (it.hasNext()) {
			TileFortronConnective connection = it.next();
			if (getFrequency() != connection.getFrequency()) {
				connection.connections.remove(this);
				it.remove();
			}
		}
	}

	protected void invalidateConnections() {
		Iterator<TileFortronConnective> it = connections.iterator();
		while (it.hasNext()) {
			TileFortronConnective connection = it.next();
			connection.connections.remove(this);
			it.remove();
		}
	}

	protected int sendFortronTo(int send, Predicate<BlockEntity> valid) {
		int sent = 0;
		HashSet<TileFortronConnective> sendList = (HashSet<TileFortronConnective>) connections.clone();
		Iterator<TileFortronConnective> it = sendList.iterator();
		while (it.hasNext()) {
			TileFortronConnective connective = it.next();
			if (!(connective.canRecieveFortron(this) && valid.test(connective))) {
				it.remove();
			}
		}
		for (TileFortronConnective connective : sendList) {
			sent += connective.recieveFortron(send / sendList.size());
		}
		return sent;
	}

	protected int getModuleCount(SubtypeModule module) {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		return inv.countItem(DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(module));
	}

	protected void tickServer(ComponentTickable tickable) {
	}

	protected int getFrequency() {
		return frequency;
	}

	protected void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	protected Predicate<BlockEntity> getConnectionTest() {
		return b -> true;
	}

	protected boolean canRecieveFortron(TileFortronConnective tile) {
		return false;
	}

	protected int recieveFortron(int amount) {
		return 0;
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		invalidateConnections();
	}

}
