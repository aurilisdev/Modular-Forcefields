package modularforcefields.common.tile;

import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import modularforcefields.common.inventory.container.ContainerBiometricIdentifier;
import modularforcefields.registers.ModularForcefieldsBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TileBiometricIdentifier extends GenericTile {

	public TileBiometricIdentifier(BlockPos pos, BlockState state) {
		super(ModularForcefieldsBlockTypes.TILE_BIOMETRICIDENTIFIER.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentTickable().tickServer(this::tickServer).tickCommon(this::tickCommon));
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::writeGuiPacket).guiPacketReader(this::readGuiPacket));
		addComponent(new ComponentInventory(this).size(9).shouldSendInfo());
		addComponent(new ComponentContainerProvider("container.biometricidentifier").createMenu((id, player) -> new ContainerBiometricIdentifier(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
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
