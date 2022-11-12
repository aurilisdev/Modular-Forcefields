package modularforcefields.common.inventory.container;

import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import electrodynamics.prefab.inventory.container.slot.item.type.SlotRestricted;
import modularforcefields.common.tile.TileBiometricIdentifier;
import modularforcefields.registers.ModularForcefieldsItems;
import modularforcefields.registers.ModularForcefieldsMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerBiometricIdentifier extends GenericContainerBlockEntity<TileBiometricIdentifier> {

	public ContainerBiometricIdentifier(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(9), new SimpleContainerData(3));
	}

	public ContainerBiometricIdentifier(int id, Inventory pinv, Container inv, ContainerData data) {
		super(ModularForcefieldsMenuTypes.CONTAINER_BIOMETRICIDENTIFIER.get(), id, pinv, inv, data);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		for (int var4 = 0; var4 < 9; var4++) {
			addSlot(new SlotRestricted(inv, var4, 8 + var4 * 18, 30, ModularForcefieldsItems.ITEM_IDENTIFICATIONCARD.get()));
		}
	}

}