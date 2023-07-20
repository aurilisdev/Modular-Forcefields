package modularforcefields.common.tile;

import org.jetbrains.annotations.NotNull;

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
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronField extends GenericTile {

	public final Property<Integer> fieldColorOrdinal = property(new Property<>(PropertyType.Integer, "fieldColor", FortronFieldColor.LIGHT_BLUE.ordinal()));
	private final Property<BlockPos> projectorPos = property(new Property<BlockPos>(PropertyType.BlockPos, "projectorPos", null).onChange(this::onPropertyChange).onLoad(this::onPropertyChange));

	private void onPropertyChange(Property<BlockPos> t, BlockPos pos) {
		if (pos != null) {
			Scheduler.schedule(3, () -> {
				if (level != null) {
					if (level.getBlockEntity(pos) instanceof TileFortronFieldProjector proj) {
						if (!level.isClientSide()) {
							fieldColorOrdinal.set(proj.getFieldColor().ordinal());
							proj.activeFields.add(this);
						}
					}
				}
			});
		}
	}

	public TileFortronField(BlockPos pos, BlockState state) {
		super(ModularForcefieldsBlockTypes.TILE_FORTRONFIELD.get(), pos, state);
		addComponent(new ComponentDirection(this));
		addComponent(new ComponentPacketHandler(this));
	}

	@Override
	public void load(@NotNull CompoundTag compound) {
		super.load(compound);
	}

	public void setConstructor(TileFortronFieldProjector projector) {
		if (!level.isClientSide()) {
			if (projector != null) {
				projectorPos.set(projector.getBlockPos());
			}
		}
	}

	public FortronFieldColor getFieldColor() {
		return FortronFieldColor.values()[fieldColorOrdinal.get()];
	}

	public BlockPos getProjectorPos() {
		return projectorPos.get();
	}
}