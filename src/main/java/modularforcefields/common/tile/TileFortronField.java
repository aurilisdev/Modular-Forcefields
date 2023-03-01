package modularforcefields.common.tile;

import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.utilities.Scheduler;
import modularforcefields.common.block.FortronFieldColor;
import modularforcefields.registers.ModularForcefieldsBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileFortronField extends GenericTile {

    public final Property<Integer> fieldColorOrdinal = property(new Property<>(PropertyType.Integer, "fieldColor", FortronFieldColor.LIGHT_BLUE.ordinal()));
    private final Property<BlockPos> projectorPos = property(new Property<>(PropertyType.BlockPos, "projectorPos", null));

    public TileFortronField(BlockPos pos, BlockState state) {
        super(ModularForcefieldsBlockTypes.TILE_FORTRONFIELD.get(), pos, state);
        addComponent(new ComponentDirection());
        addComponent(new ComponentPacketHandler());
    }

    public void setConstructor(TileFortronFieldProjector projector) {
        if (!level.isClientSide()) {
            if (projector != null) {
                fieldColorOrdinal.set(projector.getFieldColor().ordinal());
                projectorPos.setAmbigous(projector.getBlockPos());
                projectorPos.forceDirty();
                projector.activeFields.add(this);
            }
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        Scheduler.schedule(3, () -> {
            if (level.getBlockEntity(projectorPos.get()) instanceof TileFortronFieldProjector projector) {
                setConstructor(projector);
            } else {
                level.setBlockAndUpdate(getBlockPos(), Blocks.AIR.defaultBlockState());
            }
        });
        return super.getUpdateTag();
    }

    public FortronFieldColor getFieldColor() {
        return FortronFieldColor.values()[fieldColorOrdinal.get()];
    }

    public BlockPos getProjectorPos() {
        return projectorPos.get();
    }
}