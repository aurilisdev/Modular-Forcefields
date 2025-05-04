package modularforcefields.client.screen;

import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.tile.TileFortronCapacitor;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnit;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentMultiLabel;
import voltaic.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;

@OnlyIn(Dist.CLIENT)
public class ScreenFortronCapacitor extends GenericScreen<ContainerFortronCapacitor> {
	public ScreenFortronCapacitor(ContainerFortronCapacitor container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addComponent(new ScreenComponentFluidGauge(() -> {
			TileFortronCapacitor capacitor = container.getSafeHost();
			if (capacitor != null) {
				FluidTank tank = new FluidTank(capacitor.fortronCapacity.get().intValue());
				tank.setFluid(new FluidStack(ModularForcefieldsFluids.fluidFortron, capacitor.fortron.get().intValue()));
				return tank;
			}
			return null;
		}, 8, 27));
		addComponent(new ScreenComponentMultiLabel(0, 0, matrixStack -> {
			if (menu.getUnsafeHost() instanceof TileFortronCapacitor capacitor) {
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.transfer", ChatFormatter.getChatDisplayShort(capacitor.getTransfer() / 1000 * 20, DisplayUnits.BUCKETS).append(" / s")), 25, 45, 4210752, false);
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.linked", capacitor.getConnections()), 25, 55, 4210752, false);
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.frequency", capacitor.getFrequency()), 25, 35, 4210752, false);
			}
		}));
		imageHeight += 40;
		inventoryLabelY += 40;
	}
}