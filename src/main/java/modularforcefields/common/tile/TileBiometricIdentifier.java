package modularforcefields.common.tile;

import modularforcefields.common.inventory.container.ContainerBiometricIdentifier;
import modularforcefields.registers.ModularForcefieldsBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;

public class TileBiometricIdentifier extends GenericTile {

	public TileBiometricIdentifier(BlockPos pos, BlockState state) {
		super(ModularForcefieldsBlockTypes.TILE_BIOMETRICIDENTIFIER.get(), pos, state);
		addComponent(new ComponentTickable(this));
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentInventory(this, InventoryBuilder.newInv().forceSize(9)));
		addComponent(new ComponentContainerProvider("container.biometricidentifier", this).createMenu((id, player) -> new ContainerBiometricIdentifier(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
	}

}
