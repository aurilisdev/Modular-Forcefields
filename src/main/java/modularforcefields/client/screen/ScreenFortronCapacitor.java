package modularforcefields.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentFluid;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.tile.TileFortronCapacitor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
public class ScreenFortronCapacitor extends GenericScreen<ContainerFortronCapacitor> {
	public ScreenFortronCapacitor(ContainerFortronCapacitor container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		components.add(new ScreenComponentFluid(() -> {
			TileFortronCapacitor capacitor = container.getHostFromIntArray();
			if (capacitor != null) {
				FluidTank tank = new FluidTank(capacitor.fortronCapacity);
				tank.setFluid(new FluidStack(DeferredRegisters.fluidFortron, capacitor.fortron));
				return tank;
			}
			return null;
		}, this, 8, 27));
		imageHeight += 40;
		inventoryLabelY += 40;
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		super.renderLabels(matrixStack, mouseX, mouseY);
		if (menu.getUnsafeHost() instanceof TileFortronCapacitor capacitor) {
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.transfer", ChatFormatter.getChatDisplayShort(capacitor.getTransfer(), DisplayUnit.BUCKETS)), 25, 65, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.linked", capacitor.getConnections()), 25, 55, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.usage", ChatFormatter.getChatDisplayShort(capacitor.getTransfer() * 20, DisplayUnit.WATT)), 25, 45, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.frequency", capacitor.getFrequency()), 25, 35, 4210752);
		}
	}
}