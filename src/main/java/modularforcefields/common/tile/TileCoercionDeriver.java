package modularforcefields.common.tile;

import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.settings.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TileCoercionDeriver extends GenericTile {

	public TileCoercionDeriver(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_COERCIONDERIVER.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentTickable().tickServer(this::tickServer).tickCommon(this::tickCommon));
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::writeGuiPacket).guiPacketReader(this::readGuiPacket));
		addComponent(new ComponentElectrodynamic(this).voltage(Constants.COERCIONDERIVER_VOLTAGE).input(Direction.DOWN));
	}

	private void tickCommon(ComponentTickable tickable) {
	}

	private void tickServer(ComponentTickable tickable) {
	}

	private void writeGuiPacket(CompoundTag compound) {
	}

	private void readGuiPacket(CompoundTag compound) {
	}
}
