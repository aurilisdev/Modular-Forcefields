package modularforcefields.common.tile;

import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.block.FortronFieldColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronField extends GenericTile {

	private BlockPos projectorPos = BlockPos.ZERO;
	private FortronFieldColor fieldColor = FortronFieldColor.LIGHT_BLUE;

	public TileFortronField(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_FORTRONFIELD.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::saveAdditional).guiPacketReader(this::load));
	}

	public void setConstructor(TileFortronFieldProjector projector) {
		if (projector != null) {
			fieldColor = projector.getFieldColor();
			projectorPos = projector.getBlockPos();
			if (!level.isClientSide()) {
				ComponentPacketHandler handler = getComponent(ComponentType.PacketHandler);
				handler.sendGuiPacketToTracking();
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putInt("fieldColor", fieldColor.ordinal());
		if (projectorPos != null) {
			compound.putInt("px", projectorPos.getX());
			compound.putInt("py", projectorPos.getY());
			compound.putInt("pz", projectorPos.getZ());
		}
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		fieldColor = FortronFieldColor.values()[compound.getInt("fieldColor")];
		if (compound.contains("px")) {
			projectorPos = new BlockPos(compound.getInt("px"), compound.getInt("py"), compound.getInt("pz"));
		}
	}

	public FortronFieldColor getFieldColor() {
		return fieldColor;
	}

	public BlockPos getProjectorPos() {
		return projectorPos;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}