package modularforcefields.common.tile;

import modularforcefields.common.inventory.container.ContainerBiometricIdentifier;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;

public class TileBiometricIdentifier extends GenericTile {

    public TileBiometricIdentifier(BlockPos pos, BlockState state) {
	super(ModularForcefieldsTiles.TILE_BIOMETRICIDENTIFIER.get(), pos, state);
	addComponent(new ComponentTickable(this));
	addComponent(new ComponentPacketHandler(this));
	addComponent(new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().forceSize(9)));
	addComponent(new ComponentContainerProvider("biometricidentifier", this)
		.createMenu((id, player) -> new ContainerBiometricIdentifier(id, player,
			getComponent(IComponentType.Inventory), getCoordsArray())));
    }

}
