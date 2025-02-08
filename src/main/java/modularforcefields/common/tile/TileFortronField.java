package modularforcefields.common.tile;

import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyTypes;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import electrodynamics.prefab.utilities.Scheduler;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronField extends GenericTile {

    private final Property<BlockPos> projectorPos = property(
	    new Property<>(PropertyTypes.BLOCK_POS, "projectorPos", BlockEntityUtils.OUT_OF_REACH)
		    .onChange(this::onPropertyChange).onTileLoaded(prop -> onPropertyChange(prop, prop.get())));

    private void onPropertyChange(Property<BlockPos> t, BlockPos pos) {
	if (pos == null) {
	    return;
	}
	Scheduler.schedule(1, () -> {
	    if (level != null) { // This check must be here, since it may be null when calling onPropertyChange
				 // the first time.
		if (level.getBlockEntity(pos) instanceof TileFortronFieldProjector proj) {
		    if (!level.isClientSide()) {
			proj.activeFields.add(this);
		    }
		}
	    }
	});
    }

    public TileFortronField(BlockPos pos, BlockState state) {
	super(ModularForcefieldsTiles.TILE_FORTRONFIELD.get(), pos, state);
	addComponent(new ComponentPacketHandler(this));
    }

    public void setConstructor(TileFortronFieldProjector projector) {
	if (!level.isClientSide()) {
	    if (projector != null) {
		projectorPos.set(projector.getBlockPos());
	    }
	}
    }

    @Override
    public int hashCode() {
	return (int) ((10000 - getBlockPos().getY()) + level.random.nextDouble() * 3
		+ (int) Math.sqrt(getBlockPos().distToCenterSqr(getProjectorPos().getX() + 0.5,
			getBlockPos().getY() + 0.5, getProjectorPos().getZ() + 0.5)));
    }

    public BlockPos getProjectorPos() {
	return projectorPos.get();
    }
}