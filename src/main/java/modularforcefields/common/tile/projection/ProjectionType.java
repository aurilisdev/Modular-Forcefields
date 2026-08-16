package modularforcefields.common.tile.projection;

import java.util.Random;
import java.util.function.BiConsumer;

import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.core.BlockPos;
import voltaic.prefab.block.HashDistanceBlockPos;

public enum ProjectionType {

    NONE((proj, t) -> proj.calculatedFieldPoints.clear()),

    CUBE(ProjectionType::calculateCube),

    SPHERE((proj, t) -> calculateSphere(proj, t, false)),

    HEMISPHERE((proj, t) -> calculateSphere(proj, t, true)),

    PYRAMID(ProjectionType::calculatePyramid);

    private final BiConsumer<TileFortronFieldProjector, ThreadProjectorCalculationThread> calculate;

    ProjectionType(BiConsumer<TileFortronFieldProjector, ThreadProjectorCalculationThread> calculate) {
	this.calculate = calculate;
    }

    public void calculate(TileFortronFieldProjector projector, ThreadProjectorCalculationThread thread) {
	calculate.accept(projector, thread);
    }

    private static void calculateCube(TileFortronFieldProjector proj, ThreadProjectorCalculationThread thread) {

	Random rand = new Random();

	int minX = proj.xRadiusNeg.getValue();
	int maxX = proj.xRadiusPos.getValue();
	int minY = proj.yRadiusNeg.getValue();
	int maxY = proj.yRadiusPos.getValue();
	int minZ = proj.zRadiusNeg.getValue();
	int maxZ = proj.zRadiusPos.getValue();

	int projectorX = proj.getBlockPos().getX();
	int projectorZ = proj.getBlockPos().getZ();

	boolean interior = proj.isInterior();

	if (interior) {

	    for (int x = minX + 1; x < maxX; x++) {

		if (thread.isInterrupted()) {
		    return;
		}

		int dx = x - projectorX;
		int dxSq = dx * dx;

		for (int y = minY + 1; y < maxY; y++) {
		    for (int z = minZ + 1; z < maxZ; z++) {

			int dz = z - projectorZ;

			int horizontalDistance = (int) Math.sqrt(dxSq + dz * dz);

			proj.calculatedFieldPoints.add(new HashDistanceBlockPos(x, y, z,
				(int) (10000 - y + rand.nextDouble() * 3 + horizontalDistance)));
		    }
		}
	    }

	    return;
	}

	/*
	 * Shell mode:
	 *
	 * Generate the six faces directly rather than scanning the complete rectangular
	 * volume and testing isEdge for every position.
	 */

	// X faces
	for (int y = minY; y <= maxY; y++) {

	    if (thread.isInterrupted()) {
		return;
	    }

	    for (int z = minZ; z <= maxZ; z++) {

		addCubePoint(proj, rand, minX, y, z, projectorX, projectorZ);

		if (maxX != minX) {
		    addCubePoint(proj, rand, maxX, y, z, projectorX, projectorZ);
		}
	    }
	}

	// Y faces, excluding X faces already generated
	for (int x = minX + 1; x < maxX; x++) {

	    if (thread.isInterrupted()) {
		return;
	    }

	    for (int z = minZ; z <= maxZ; z++) {

		addCubePoint(proj, rand, x, minY, z, projectorX, projectorZ);

		if (maxY != minY) {
		    addCubePoint(proj, rand, x, maxY, z, projectorX, projectorZ);
		}
	    }
	}

	// Z faces, excluding X/Y faces already generated
	for (int x = minX + 1; x < maxX; x++) {

	    if (thread.isInterrupted()) {
		return;
	    }

	    for (int y = minY + 1; y < maxY; y++) {

		addCubePoint(proj, rand, x, y, minZ, projectorX, projectorZ);

		if (maxZ != minZ) {
		    addCubePoint(proj, rand, x, y, maxZ, projectorX, projectorZ);
		}
	    }
	}
    }

    private static void addCubePoint(TileFortronFieldProjector proj, Random rand, int x, int y, int z, int projectorX,
	    int projectorZ) {

	int dx = x - projectorX;
	int dz = z - projectorZ;

	int horizontalDistance = (int) Math.sqrt(dx * dx + dz * dz);

	proj.calculatedFieldPoints
		.add(new HashDistanceBlockPos(x, y, z, (int) (10000 - y + rand.nextDouble() * 3 + horizontalDistance)));
    }

    private static void calculateSphere(TileFortronFieldProjector proj, ThreadProjectorCalculationThread thread,
	    boolean hemisphere) {

	Random rand = new Random();

	BlockPos center = proj.getShiftedPos();

	int centerX = center.getX();
	int centerY = center.getY();
	int centerZ = center.getZ();

	int radius = proj.radius.getValue();

	boolean interior = proj.isInterior();

	int radiusSq = radius * radius;
	int outer = radius + 1;
	int outerSq = outer * outer;

	int minX = centerX - radius;
	int maxX = centerX + radius;

	int minY = hemisphere ? Math.max(proj.getLevel().getMinBuildHeight(), centerY)
		: Math.max(proj.getLevel().getMinBuildHeight(), centerY - radius);

	int maxY = Math.min(proj.getLevel().getMaxBuildHeight() - 1, centerY + radius);

	int minZ = centerZ - radius;
	int maxZ = centerZ + radius;

	for (int x = minX; x <= maxX; x++) {

	    if (thread.isInterrupted()) {
		return;
	    }

	    int dx = x - centerX;
	    int dxSq = dx * dx;

	    for (int y = minY; y <= maxY; y++) {

		int dy = y - centerY;
		int dxySq = dxSq + dy * dy;

		/*
		 * Even z=0 would already be outside the sphere.
		 */
		if (dxySq >= outerSq) {
		    continue;
		}

		for (int z = minZ; z <= maxZ; z++) {

		    int dz = z - centerZ;
		    int distanceSq = dxySq + dz * dz;

		    boolean valid = interior ? distanceSq < outerSq : distanceSq >= radiusSq && distanceSq < outerSq;

		    if (!valid) {
			continue;
		    }

		    proj.calculatedFieldPoints
			    .add(new HashDistanceBlockPos(x, y, z, (int) (10000 - y + rand.nextDouble() * 3)));
		}
	    }
	}
	if (hemisphere && !interior && centerY > proj.getLevel().getMinBuildHeight()) {
	    int y = centerY - 1;

	    for (int x = minX; x <= maxX; x++) {
		if (thread.isInterrupted()) {
		    return;
		}

		int dx = x - centerX;
		int dxSq = dx * dx;

		for (int z = minZ; z <= maxZ; z++) {
		    int dz = z - centerZ;

		    if (dxSq + dz * dz >= outerSq) {
			continue;
		    }

		    proj.calculatedFieldPoints
			    .add(new HashDistanceBlockPos(x, y, z, (int) (10000 - y + rand.nextDouble() * 3)));
		}
	    }
	}
    }

    private static void calculatePyramid(TileFortronFieldProjector proj, ThreadProjectorCalculationThread thread) {

	Random rand = new Random();

	BlockPos center = proj.getShiftedPos();

	int centerX = center.getX();
	int centerY = center.getY();
	int centerZ = center.getZ();

	int radius = proj.radius.getValue();

	boolean interior = proj.isInterior();

	int minY = Math.max(proj.getLevel().getMinBuildHeight(), centerY);

	int maxY = Math.min(proj.getLevel().getMaxBuildHeight() - 1, centerY + radius);

	for (int x = centerX - radius; x <= centerX + radius; x++) {

	    if (thread.isInterrupted()) {
		return;
	    }

	    int dx = Math.abs(x - centerX);

	    for (int y = minY; y <= maxY; y++) {

		int dxy = dx + Math.abs(y - centerY);

		if (dxy > radius) {
		    continue;
		}

		for (int z = centerZ - radius; z <= centerZ + radius; z++) {

		    int distance = dxy + Math.abs(z - centerZ);

		    if (interior ? distance <= radius : distance == radius) {

			proj.calculatedFieldPoints
				.add(new HashDistanceBlockPos(x, y, z, (int) (10000 - y + rand.nextDouble() * 3)));
		    }
		}
	    }
	}
	if (!interior && centerY > proj.getLevel().getMinBuildHeight()) {
	    int y = centerY - 1;

	    for (int x = centerX - radius; x <= centerX + radius; x++) {
		if (thread.isInterrupted()) {
		    return;
		}

		int dx = Math.abs(x - centerX);
		for (int z = centerZ - radius; z <= centerZ + radius; z++) {
		    int dz = Math.abs(z - centerZ);

		    if (dx + dz > radius) {
			continue;
		    }

		    proj.calculatedFieldPoints
			    .add(new HashDistanceBlockPos(x, y, z, (int) (10000 - y + rand.nextDouble() * 3)));
		}
	    }
	}
    }
}