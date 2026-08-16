package modularforcefields.common.world;

import javax.annotation.Nullable;

import modularforcefields.common.tile.projection.ProjectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public final class FortronProtectionRegion {

    private final long projectorId;
    private final ProjectionType type;

    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    private final int centerX;
    private final int centerY;
    private final int centerZ;
    private final int radius;

    private FortronProtectionRegion(long projectorId, ProjectionType type, int minX, int minY, int minZ, int maxX,
	    int maxY, int maxZ, int centerX, int centerY, int centerZ, int radius) {
	this.projectorId = projectorId;
	this.type = type;
	this.minX = minX;
	this.minY = minY;
	this.minZ = minZ;
	this.maxX = maxX;
	this.maxY = maxY;
	this.maxZ = maxZ;
	this.centerX = centerX;
	this.centerY = centerY;
	this.centerZ = centerZ;
	this.radius = radius;
    }

    public static FortronProtectionRegion cube(long projectorId, int minX, int minY, int minZ, int maxX, int maxY,
	    int maxZ) {

	return new FortronProtectionRegion(projectorId, ProjectionType.CUBE, minX, minY, minZ, maxX, maxY, maxZ, 0, 0,
		0, 0);
    }

    public static FortronProtectionRegion sphere(long projectorId, BlockPos center, int radius) {
	return centered(projectorId, ProjectionType.SPHERE, center, radius);
    }

    public static FortronProtectionRegion hemisphere(long projectorId, BlockPos center, int radius) {
	return centered(projectorId, ProjectionType.HEMISPHERE, center, radius);
    }

    public static FortronProtectionRegion pyramid(long projectorId, BlockPos center, int radius) {
	return centered(projectorId, ProjectionType.PYRAMID, center, radius);
    }

    private static FortronProtectionRegion centered(long projectorId, ProjectionType type, BlockPos center,
	    int radius) {

	return new FortronProtectionRegion(projectorId, type, 0, 0, 0, 0, 0, 0, center.getX(), center.getY(),
		center.getZ(), radius);
    }

    public long getProjectorId() {
	return projectorId;
    }

    public ProjectionType getType() {
	return type;
    }

    public boolean contains(BlockPos pos) {
	return contains(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean contains(int x, int y, int z) {
	switch (type) {
	case CUBE:
	    return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;

	case SPHERE: {
	    long dx = (long) x - centerX;
	    long dy = (long) y - centerY;
	    long dz = (long) z - centerZ;
	    long outerRadius = radius + 1L;

	    return dx * dx + dy * dy + dz * dz < outerRadius * outerRadius;
	}

	case HEMISPHERE: {
	    if (y < centerY - 1) {
		return false;
	    }

	    long dx = (long) x - centerX;
	    long dz = (long) z - centerZ;
	    long outerRadius = radius + 1L;

	    /*
	     * The new floor is a full disk one block below the hemisphere.
	     */
	    if (y == centerY - 1) {
		return dx * dx + dz * dz < outerRadius * outerRadius;
	    }

	    long dy = (long) y - centerY;

	    return dx * dx + dy * dy + dz * dz < outerRadius * outerRadius;
	}

	case PYRAMID: {
	    if (y < centerY - 1) {
		return false;
	    }

	    long dx = Math.abs((long) x - centerX);
	    long dz = Math.abs((long) z - centerZ);

	    /*
	     * At centerY - 1 this gives the complete bottom diamond. Above centerY it
	     * follows the existing pyramid equation.
	     */
	    long dy = Math.max(0L, (long) y - centerY);

	    return dx + dy + dz <= radius;
	}

	default:
	    return false;
	}
    }

    /*
     * A closed forcefield blocks an explosion exactly when the source and target
     * are on opposite sides of its boundary.
     */
    public boolean separates(BlockPos source, BlockPos target) {
	return contains(source) != contains(target);
    }

    public boolean intersects(BlockPos center, int range) {
	int queryMinX = center.getX() - range;
	int queryMinY = center.getY() - range;
	int queryMinZ = center.getZ() - range;
	int queryMaxX = center.getX() + range;
	int queryMaxY = center.getY() + range;
	int queryMaxZ = center.getZ() + range;

	int regionMinX;
	int regionMinY;
	int regionMinZ;
	int regionMaxX;
	int regionMaxY;
	int regionMaxZ;

	if (type == ProjectionType.CUBE) {
	    regionMinX = minX;
	    regionMinY = minY;
	    regionMinZ = minZ;
	    regionMaxX = maxX;
	    regionMaxY = maxY;
	    regionMaxZ = maxZ;
	} else {
	    regionMinX = centerX - radius;
	    regionMaxX = centerX + radius;
	    regionMinZ = centerZ - radius;
	    regionMaxZ = centerZ + radius;

	    if (type == ProjectionType.SPHERE) {
		regionMinY = centerY - radius;
	    } else {
		regionMinY = centerY - 1;
	    }

	    regionMaxY = centerY + radius;
	}

	return queryMaxX >= regionMinX && queryMinX <= regionMaxX && queryMaxY >= regionMinY && queryMinY <= regionMaxY
		&& queryMaxZ >= regionMinZ && queryMinZ <= regionMaxZ;
    }

    public CompoundTag save() {
	CompoundTag tag = new CompoundTag();

	tag.putString("type", type.name());

	if (type == ProjectionType.CUBE) {
	    tag.putInt("minX", minX);
	    tag.putInt("minY", minY);
	    tag.putInt("minZ", minZ);
	    tag.putInt("maxX", maxX);
	    tag.putInt("maxY", maxY);
	    tag.putInt("maxZ", maxZ);
	} else {
	    tag.putInt("centerX", centerX);
	    tag.putInt("centerY", centerY);
	    tag.putInt("centerZ", centerZ);
	    tag.putInt("radius", radius);
	}

	return tag;
    }

    public static @Nullable FortronProtectionRegion load(long projectorId, CompoundTag tag) {
	ProjectionType type;

	try {
	    type = ProjectionType.valueOf(tag.getString("type"));
	} catch (IllegalArgumentException exception) {
	    return null;
	}

	if (type == ProjectionType.CUBE) {
	    return cube(projectorId, tag.getInt("minX"), tag.getInt("minY"), tag.getInt("minZ"), tag.getInt("maxX"),
		    tag.getInt("maxY"), tag.getInt("maxZ"));
	}

	BlockPos center = new BlockPos(tag.getInt("centerX"), tag.getInt("centerY"), tag.getInt("centerZ"));

	int radius = tag.getInt("radius");

	return switch (type) {
	case SPHERE -> sphere(projectorId, center, radius);
	case HEMISPHERE -> hemisphere(projectorId, center, radius);
	case PYRAMID -> pyramid(projectorId, center, radius);
	default -> null;
	};
    }
}