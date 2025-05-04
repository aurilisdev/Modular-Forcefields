package modularforcefields.client.screen;

import modularforcefields.common.inventory.container.ContainerBiometricIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import voltaic.prefab.screen.GenericScreen;

@OnlyIn(Dist.CLIENT)
public class ScreenBiometricIdentifier extends GenericScreen<ContainerBiometricIdentifier> {
	public ScreenBiometricIdentifier(ContainerBiometricIdentifier container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
	}
}