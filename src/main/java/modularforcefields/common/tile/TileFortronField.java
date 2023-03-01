package modularforcefields.common.tile;

import electrodynamics.common.tile.quarry.TileQuarry;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import modularforcefields.common.block.FortronFieldColor;
import modularforcefields.registers.ModularForcefieldsBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronField extends GenericTile {

	public final Property<Integer> fieldColorOrdinal = property(new Property<>(PropertyType.Integer, "fieldColor", FortronFieldColor.LIGHT_BLUE.ordinal()));
	private final Property<BlockPos> projectorPos = property(new Property<>(PropertyType.BlockPos, "projectorPos", TileQuarry.OUT_OF_REACH));

	public TileFortronField(BlockPos pos, BlockState state) {
		super(ModularForcefieldsBlockTypes.TILE_FORTRONFIELD.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler());
	}

	public void setConstructor(TileFortronFieldProjector projector) {
		if (!level.isClientSide()) {
			if (projector != null) {
				fieldColorOrdinal.set(projector.getFieldColor().ordinal());
				if (projectorPos.get() != BlockPos.ZERO) {
					projectorPos.set(new BlockPos(projector.getBlockPos()));
				}
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