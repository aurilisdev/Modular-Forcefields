package modularforcefields.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentFluid;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import modularforcefields.common.tile.TileFortronCapacitor;
import modularforcefields.common.tile.TileInterdictionMatrix;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
public class ScreenInterdictionMatrix extends GenericScreen<ContainerInterdictionMatrix> {
	public ScreenInterdictionMatrix(ContainerInterdictionMatrix container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		components.add(new ScreenComponentFluid(() -> {
			TileInterdictionMatrix matrix = container.getHostFromIntArray();
			if (matrix != null) {
				FluidTank tank = new FluidTank(matrix.fortronCapacity);
				tank.setFluid(new FluidStack(DeferredRegisters.fluidFortron, matrix.fortron));
				return tank;
			}
			return null;
		}, this, 8, 60));
		imageHeight += 51;
		inventoryLabelY += 51;
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