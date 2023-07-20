package modularforcefields.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.gauges.ScreenComponentFluidGaugeInput;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.tile.TileCoercionDeriver;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
public class ScreenCoercionDeriver extends GenericScreen<ContainerCoercionDeriver> {
	public ScreenCoercionDeriver(ContainerCoercionDeriver container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addComponent(new ScreenComponentFluidGaugeInput(() -> {
			TileCoercionDeriver deriver = container.getHostFromIntArray();
			if (deriver != null) {
				FluidTank tank = new FluidTank(deriver.fortronCapacity.get());
				tank.setFluid(new FluidStack(ModularForcefieldsFluids.fluidFortron, deriver.fortron.get()));
				return tank;
			}
			return null;
		}, 8, 27));
		addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(electro -> electro.getHolder() instanceof TileCoercionDeriver deriver ? (double) deriver.fortron.get() : 0));
		imageHeight += 40;
		inventoryLabelY += 40;
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		super.renderLabels(matrixStack, mouseX, mouseY);
		if (menu.getUnsafeHost() instanceof TileCoercionDeriver deriver) {
			font.draw(matrixStack, MFFSTextUtils.gui("fortrondevice.transfer", ChatFormatter.getChatDisplayShort(deriver.getTransfer() * 20, DisplayUnit.BUCKETS).append(" / s")), 25, 65, 4210752);
			font.draw(matrixStack, MFFSTextUtils.gui("fortrondevice.linked", deriver.getConnections()), 25, 55, 4210752);
			font.draw(matrixStack, MFFSTextUtils.gui("fortrondevice.usage", ChatFormatter.getChatDisplayShort(deriver.getTransfer() * 20, DisplayUnit.WATT)), 25, 45, 4210752);
			font.draw(matrixStack, MFFSTextUtils.gui("fortrondevice.frequency", deriver.getFrequency()), 25, 35, 4210752);
		}
	}
}