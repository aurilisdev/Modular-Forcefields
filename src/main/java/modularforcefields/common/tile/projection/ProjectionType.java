package modularforcefields.common.tile.projection;

import electrodynamics.prefab.block.HashDistanceBlockPos;
import electrodynamics.prefab.utilities.object.Location;
import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.core.BlockPos;

import java.util.function.BiConsumer;

public enum ProjectionType {
    NONE((proj, t) -> proj.calculatedFieldPoints.clear()), CUBE((proj, t) -> {
        for (int i = proj.xRadiusNeg.get(); i <= proj.xRadiusPos.get(); i++) {
            for (int j = proj.yRadiusNeg.get(); j <= proj.yRadiusPos.get(); j++) {
                for (int k = proj.zRadiusNeg.get(); k <= proj.zRadiusPos.get(); k++) {
                    boolean isEdge = i == proj.xRadiusNeg.get() || i == proj.xRadiusPos.get() || j == proj.yRadiusNeg.get() || j == proj.yRadiusPos.get() || k == proj.zRadiusNeg.get() || k == proj.zRadiusPos.get();
                    if (proj.isInterior() != isEdge) {
                        proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, 10000 - j));
                    }
                }
            }
        }
    }), SPHERE((proj, t) -> {
        BlockPos shifted = proj.getShiftedPos();
        for (int i = shifted.getY() - proj.radius.get(); i <= shifted.getX() + proj.radius.get(); i++) {
            for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY() - proj.radius.get()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius.get()); j++) {
                for (int k = shifted.getZ() - proj.radius.get(); k <= shifted.getZ() + proj.radius.get(); k++) {
                    Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
                    int distance = (int) loc.distance(new Location(shifted));
                    if (proj.isInterior() ? distance <= proj.radius.get() : distance == proj.radius.get()) {
                        proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, 10000 - j));
                    }
                }
            }
        }
    }), HEMISPHERE((proj, t) -> {
        BlockPos shifted = proj.getShiftedPos();
        for (int i = shifted.getY() - proj.radius.get(); i <= shifted.getX() + proj.radius.get(); i++) {
            for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius.get()); j++) {
                for (int k = shifted.getZ() - proj.radius.get(); k <= shifted.getZ() + proj.radius.get(); k++) {
                    Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
                    int distance = (int) loc.distance(new Location(shifted));
                    if (proj.isInterior() ? distance <= proj.radius.get() : distance == proj.radius.get()) {
                        proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, 10000 - j));
                    }
                }
            }
        }
    }), PYRAMID((proj, t) -> {
        BlockPos shifted = proj.getShiftedPos();
        for (int i = shifted.getY() - proj.radius.get(); i <= shifted.getX() + proj.radius.get(); i++) {
            for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius.get()); j++) {
                for (int k = shifted.getZ() - proj.radius.get(); k <= shifted.getZ() + proj.radius.get(); k++) {
                    if (t.isInterrupted()) {
                        return;
                    }
                    Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
                    int distance = (int) loc.distancelinear(new Location(shifted));
                    if (proj.isInterior() ? distance <= proj.radius.get() : distance == proj.radius.get()) {
                        proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, 10000 - j));
                    }
                }
            }
        }
    });

    private final BiConsumer<TileFortronFieldProjector, ThreadProjectorCalculationThread> calculate;

    ProjectionType(BiConsumer<TileFortronFieldProjector, ThreadProjectorCalculationThread> calculate) {
        this.calculate = calculate;
    }

    public void calculate(TileFortronFieldProjector projector, ThreadProjectorCalculationThread thread) {
        calculate.accept(projector, thread);
    }
}
