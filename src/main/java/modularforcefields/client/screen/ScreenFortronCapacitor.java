package modularforcefields.client.screen;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.ScreenComponentMultiLabel;
import electrodynamics.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.tile.TileFortronCapacitor;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class ScreenFortronCapacitor extends GenericScreen<ContainerFortronCapacitor> {
	public ScreenFortronCapacitor(ContainerFortronCapacitor container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addComponent(new ScreenComponentFluidGauge(() -> {
			TileFortronCapacitor capacitor = container.getSafeHost();
			if (capacitor != null) {
				FluidTank tank = new FluidTank(capacitor.fortronCapacity.get().intValue());
				tank.setFluid(new FluidStack(ModularForcefieldsFluids.FLUID_FORTRON, capacitor.fortron.get().intValue()));
				return tank;
			}
			return null;
		}, 8, 27));
		addComponent(new ScreenComponentMultiLabel(0, 0, matrixStack -> {
			if (menu.getUnsafeHost() instanceof TileFortronCapacitor capacitor) {
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.transfer", ChatFormatter.getChatDisplayShort((int)(capacitor.getTransfer() / 1000.0 * 20), DisplayUnit.BUCKETS).append(" / s")), 25, 45, 4210752, false);
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.linked", capacitor.getConnections()), 25, 55, 4210752, false);
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.frequency", capacitor.getFrequency()), 25, 35, 4210752, false);
			}
		}));
		imageHeight += 40;
		inventoryLabelY += 40;
	}
}