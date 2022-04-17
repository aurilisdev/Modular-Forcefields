package modularforcefields.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentFluid;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
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
		if (menu.getUnsafeHost() instanceof TileInterdictionMatrix matrix) {
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.transfer", ChatFormatter.getChatDisplayShort(matrix.getFortronUse(), DisplayUnit.BUCKETS)), 25, 105, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.linked", matrix.getConnections()), 25, 95, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.usage", ChatFormatter.getChatDisplayShort(matrix.getFortronUse() * 20, DisplayUnit.WATT)), 25, 85, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.frequency", matrix.getFrequency()), 25, 75, 4210752);
		}
	}
}