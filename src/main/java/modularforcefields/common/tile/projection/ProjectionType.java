package modularforcefields.common.tile.projection;

import java.util.function.BiConsumer;

import electrodynamics.prefab.block.HashDistanceBlockPos;
import electrodynamics.prefab.utilities.object.Location;
import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.core.BlockPos;

public enum ProjectionType {
	NONE((proj, t) -> proj.calculatedFieldPoints.clear()),
	CUBE((proj, t) -> {
		for (int i = proj.xRadiusNeg; i <= proj.xRadiusPos; i++) {
			for (int j = proj.yRadiusNeg; j <= proj.yRadiusPos; j++) {
				for (int k = proj.zRadiusNeg; k <= proj.zRadiusPos; k++) {

					boolean isEdge = i == proj.xRadiusNeg || i == proj.xRadiusPos || j == proj.yRadiusNeg || j == proj.yRadiusPos || k == proj.zRadiusNeg || k == proj.zRadiusPos;
					if (proj.isInterior() != isEdge) {
						proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, 10000 - j));
					}
				}
			}
		}
	}),
	SPHERE((proj, t) -> {
		BlockPos shifted = proj.getShiftedPos();
		for (int i = shifted.getY() - proj.radius; i <= shifted.getX() + proj.radius; i++) {
			for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY() - proj.radius); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius); j++) {
				for (int k = shifted.getZ() - proj.radius; k <= shifted.getZ() + proj.radius; k++) {
					Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
					int distance = (int) loc.distance(new Location(shifted));
					if (proj.isInterior() ? distance <= proj.radius : distance == proj.radius) {
						proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, 10000 - j));
					}
				}
			}
		}
	}),
	HEMISPHERE((proj, t) -> {
		BlockPos shifted = proj.getShiftedPos();
		for (int i = shifted.getY() - proj.radius; i <= shifted.getX() + proj.radius; i++) {
			for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius); j++) {
				for (int k = shifted.getZ() - proj.radius; k <= shifted.getZ() + proj.radius; k++) {
					Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
					int distance = (int) loc.distance(new Location(shifted));
					if (proj.isInterior() ? distance <= proj.radius : distance == proj.radius) {
						proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, 10000 - j));
					}
				}
			}
		}
	}),
	PYRAMID((proj, t) -> {
		BlockPos shifted = proj.getShiftedPos();
		for (int i = shifted.getY() - proj.radius; i <= shifted.getX() + proj.radius; i++) {
			for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius); j++) {
				for (int k = shifted.getZ() - proj.radius; k <= shifted.getZ() + proj.radius; k++) {
					if (t.isInterrupted()) {
						return;
					}
					Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
					int distance = (int) loc.distancelinear(new Location(shifted));
					if (proj.isInterior() ? distance <= proj.radius : distance == proj.radius) {
						proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, 10000 - j));
					}
				}
			}
		}
	});

	private BiConsumer<TileFortronFieldProjector, ThreadProjectorCalculationThread> calculate;

	ProjectionType(BiConsumer<TileFortronFieldProjector, ThreadProjectorCalculationThread> calculate) {
		this.calculate = calculate;
	}

	public void calculate(TileFortronFieldProjector projector, ThreadProjectorCalculationThread thread) {
		calculate.accept(projector, thread);
	}
}
