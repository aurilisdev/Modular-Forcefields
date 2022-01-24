package modularforcefields.common.tile.projection;

import java.util.function.BiConsumer;

import electrodynamics.prefab.utilities.object.Location;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.core.BlockPos;

public enum ProjectionType {
	NONE((proj, t) -> proj.calculatedFieldPoints.clear()),
	CUBE((proj, t) -> {
		BlockPos shifted = proj.getShiftedPos();
		int xRadiusPos = shifted.getX() + Math.min(64, proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_EAST[0], ContainerFortronFieldProjector.SLOT_EAST[1]));
		int yRadiusPos = Math.min(proj.getLevel().getMaxBuildHeight(), Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY() + proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_UP[0], ContainerFortronFieldProjector.SLOT_UP[1])));
		int zRadiusPos = shifted.getZ() + Math.min(64, proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_SOUTH[0], ContainerFortronFieldProjector.SLOT_SOUTH[1]));
		int xRadiusNeg = shifted.getX() - Math.min(64, proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_WEST[0], ContainerFortronFieldProjector.SLOT_WEST[1]));
		int yRadiusNeg = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY() - proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_DOWN[0], ContainerFortronFieldProjector.SLOT_DOWN[1]));
		int zRadiusNeg = shifted.getZ() - Math.min(64, proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_NORTH[0], ContainerFortronFieldProjector.SLOT_NORTH[1]));
		for (int i = xRadiusNeg; i <= xRadiusPos; i++) {
			for (int j = yRadiusNeg; j <= yRadiusPos; j++) {
				for (int k = zRadiusNeg; k <= zRadiusPos; k++) {
					if (t.isInterrupted()) {
						return;
					}
					boolean isEdge = i == xRadiusNeg || i == xRadiusPos || j == yRadiusNeg || j == yRadiusPos || k == zRadiusNeg || k == zRadiusPos;
					if (proj.isInterior() != isEdge) {
						proj.calculatedFieldPoints.add(new BlockPos(i, j, k));
					}
				}
			}
		}
	}),
	SPHERE((proj, t) -> {
		BlockPos shifted = proj.getShiftedPos();
		int radius = Math.min(64, proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_MODULES[0], ContainerFortronFieldProjector.SLOT_MODULES[ContainerFortronFieldProjector.SLOT_MODULES.length - 1]) / 6);
		for (int i = shifted.getY() - radius; i <= shifted.getX() + radius; i++) {
			for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY() - radius); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + radius); j++) {
				for (int k = shifted.getZ() - radius; k <= shifted.getZ() + radius; k++) {
					if (t.isInterrupted()) {
						return;
					}
					Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
					int distance = (int) loc.distance(new Location(shifted));
					if (proj.isInterior() ? distance <= radius : distance == radius) {
						proj.calculatedFieldPoints.add(loc.toBlockPos());
					}
				}
			}
		}
	}),
	HEMISPHERE((proj, t) -> {
		BlockPos shifted = proj.getShiftedPos();
		int radius = Math.min(64, proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_MODULES[0], ContainerFortronFieldProjector.SLOT_MODULES[ContainerFortronFieldProjector.SLOT_MODULES.length - 1]) / 6);
		for (int i = shifted.getY() - radius; i <= shifted.getX() + radius; i++) {
			for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + radius); j++) {
				for (int k = shifted.getZ() - radius; k <= shifted.getZ() + radius; k++) {
					if (t.isInterrupted()) {
						return;
					}
					Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
					int distance = (int) loc.distance(new Location(shifted));
					if (proj.isInterior() ? distance <= radius : distance == radius) {
						proj.calculatedFieldPoints.add(loc.toBlockPos());
					}
				}
			}
		}
	}),
	PYRAMID((proj, t) -> {
		BlockPos shifted = proj.getShiftedPos();
		int radius = Math.min(64, proj.countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_MODULES[0], ContainerFortronFieldProjector.SLOT_MODULES[ContainerFortronFieldProjector.SLOT_MODULES.length - 1]) / 6);
		for (int i = shifted.getY() - radius; i <= shifted.getX() + radius; i++) {
			for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + radius); j++) {
				for (int k = shifted.getZ() - radius; k <= shifted.getZ() + radius; k++) {
					if (t.isInterrupted()) {
						return;
					}
					Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
					int distance = (int) loc.distancelinear(new Location(shifted));
					if (proj.isInterior() ? distance <= radius : distance == radius) {
						proj.calculatedFieldPoints.add(loc.toBlockPos());
					}
				}
			}
		}
	});

	public BiConsumer<TileFortronFieldProjector, ThreadProjectorCalculationThread> calculate;

	ProjectionType(BiConsumer<TileFortronFieldProjector, ThreadProjectorCalculationThread> calculate) {
		this.calculate = calculate;
	}
}
