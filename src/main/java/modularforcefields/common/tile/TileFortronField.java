package modularforcefields.common.tile;

import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.utilities.object.Location;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.block.FortronFieldColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronField extends GenericTile {

	private Location projectorPos = null;
	private FortronFieldColor fieldColor = FortronFieldColor.LIGHT_BLUE;

	public TileFortronField(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_FORTRONFIELD.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::saveAdditional).guiPacketReader(this::load));
	}

	public void setConstructor(TileFortronFieldProjector projector) {
		if (!level.isClientSide()) {
			if (projector != null) {
				ComponentPacketHandler handler = getComponent(ComponentType.PacketHandler);
				handler.sendGuiPacketToTracking();
				fieldColor = projector.getFieldColor();
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putInt("fieldColor", fieldColor.ordinal());
		if (projectorPos != null) {
			projectorPos.writeToNBT(compound, "projectorPos");
		}
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		fieldColor = FortronFieldColor.values()[compound.getInt("fieldColor")];
		projectorPos = Location.readFromNBT(compound, "projectorPos");
	}

	public FortronFieldColor getFieldColor() {
		return fieldColor;
	}

	public Location getProjectorPos() {
		return projectorPos;
	}

	@Override
	public int hashCode() {
		return worldPosition.getX() * 2 * worldPosition.getY() * worldPosition.getZ();
	}
}
