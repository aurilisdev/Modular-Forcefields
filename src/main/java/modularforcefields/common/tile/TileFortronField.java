package modularforcefields.common.tile;

import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import modularforcefields.common.block.FortronFieldColor;
import modularforcefields.registers.ModularForcefieldsBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronField extends GenericTile {

	public Property<Integer> fieldColorOrdinal = property(new Property<Integer>(PropertyType.Integer, "fieldColor")).set(FortronFieldColor.LIGHT_BLUE.ordinal()).save();
	private BlockPos projectorPos = BlockPos.ZERO;

	public TileFortronField(BlockPos pos, BlockState state) {
		super(ModularForcefieldsBlockTypes.TILE_FORTRONFIELD.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler());
	}

	public void setConstructor(TileFortronFieldProjector projector) {
		if (!level.isClientSide()) {
			if (projector != null) {
				fieldColorOrdinal.set(projector.getFieldColor().ordinal());
				if (projectorPos != BlockPos.ZERO) {
					projectorPos = new BlockPos(projector.getBlockPos());
					ComponentPacketHandler handler = getComponent(ComponentType.PacketHandler);
					handler.sendGuiPacketToTracking();
				}
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		if (projectorPos != BlockPos.ZERO && projectorPos != null) {
			compound.putInt("px", projectorPos.getX());
			compound.putInt("py", projectorPos.getY());
			compound.putInt("pz", projectorPos.getZ());
		}

	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		if (compound.contains("px")) {
			projectorPos = new BlockPos(compound.getInt("px"), compound.getInt("py"), compound.getInt("pz"));
		}
	}

	public FortronFieldColor getFieldColor() {
		return FortronFieldColor.values()[fieldColorOrdinal.get()];
	}

	public BlockPos getProjectorPos() {
		return projectorPos;
	}
}