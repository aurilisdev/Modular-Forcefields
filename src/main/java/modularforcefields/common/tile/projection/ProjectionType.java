package modularforcefields.common.tile.projection;

import java.util.Random;
import java.util.function.BiConsumer;

import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.core.BlockPos;
import voltaic.prefab.block.HashDistanceBlockPos;
import voltaic.prefab.utilities.object.Location;

public enum ProjectionType {
    NONE((proj, t) -> proj.calculatedFieldPoints.clear()), CUBE((proj, t) -> {
        Random rand = new Random();
        for (int i = proj.xRadiusNeg.getValue(); i <= proj.xRadiusPos.getValue(); i++) {
            for (int j = proj.yRadiusNeg.getValue(); j <= proj.yRadiusPos.getValue(); j++) {
                for (int k = proj.zRadiusNeg.getValue(); k <= proj.zRadiusPos.getValue(); k++) {
                    boolean isEdge = i == proj.xRadiusNeg.getValue() || i == proj.xRadiusPos.getValue() || j == proj.yRadiusNeg.getValue() || j == proj.yRadiusPos.getValue() || k == proj.zRadiusNeg.getValue() || k == proj.zRadiusPos.getValue();
                    if (proj.isInterior() != isEdge) {
                        proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, (int) ((10000 - j) + rand.nextDouble() * 3 + (int) Math.sqrt(new BlockPos(i, j, k).distToCenterSqr(proj.getBlockPos().getX() + 0.5, j + 0.5, proj.getBlockPos().getZ() + 0.5)))));
                    }
                }
            }
        }
    }), SPHERE((proj, t) -> {
        Random rand = new Random();
        BlockPos shifted = proj.getShiftedPos();
        for (int i = shifted.getY() - proj.radius.getValue(); i <= shifted.getX() + proj.radius.getValue(); i++) {
            for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY() - proj.radius.getValue()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius.getValue()); j++) {
                for (int k = shifted.getZ() - proj.radius.getValue(); k <= shifted.getZ() + proj.radius.getValue(); k++) {
                    Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
                    int distance = (int) loc.distance(new Location(shifted));
                    if (proj.isInterior() ? distance <= proj.radius.getValue() : distance == proj.radius.getValue()) {
                        proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, (int) (10000 - j + rand.nextDouble() * 3)));
                    }
                }
            }
        }
    }), HEMISPHERE((proj, t) -> {
        Random rand = new Random();
        BlockPos shifted = proj.getShiftedPos();
        for (int i = shifted.getY() - proj.radius.getValue(); i <= shifted.getX() + proj.radius.getValue(); i++) {
            for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius.getValue()); j++) {
                for (int k = shifted.getZ() - proj.radius.getValue(); k <= shifted.getZ() + proj.radius.getValue(); k++) {
                    Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
                    int distance = (int) loc.distance(new Location(shifted));
                    if (proj.isInterior() ? distance <= proj.radius.getValue() : distance == proj.radius.getValue()) {
                        proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, (int) (10000 - j + rand.nextDouble() * 3)));
                    }
                }
            }
        }
    }), PYRAMID((proj, t) -> {
        Random rand = new Random();
        BlockPos shifted = proj.getShiftedPos();
        for (int i = shifted.getY() - proj.radius.getValue(); i <= shifted.getX() + proj.radius.getValue(); i++) {
            for (int j = Math.max(proj.getLevel().getMinBuildHeight(), shifted.getY()); j <= Math.min(proj.getLevel().getMaxBuildHeight(), shifted.getY() + proj.radius.getValue()); j++) {
                for (int k = shifted.getZ() - proj.radius.getValue(); k <= shifted.getZ() + proj.radius.getValue(); k++) {
                    if (t.isInterrupted()) {
                        return;
                    }
                    Location loc = new Location(i + 0.5f, j + 0.5f, k + 0.5f);
                    int distance = (int) loc.distancelinear(new Location(shifted));
                    if (proj.isInterior() ? distance <= proj.radius.getValue() : distance == proj.radius.getValue()) {
                        proj.calculatedFieldPoints.add(new HashDistanceBlockPos(i, j, k, (int) (10000 - j + rand.nextDouble() * 3)));
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
