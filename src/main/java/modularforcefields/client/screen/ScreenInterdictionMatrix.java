package modularforcefields.client.screen;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.ScreenComponentMultiLabel;
import electrodynamics.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import modularforcefields.common.tile.TileInterdictionMatrix;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class ScreenInterdictionMatrix extends GenericScreen<ContainerInterdictionMatrix> {
	public ScreenInterdictionMatrix(ContainerInterdictionMatrix container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addComponent(new ScreenComponentFluidGauge(() -> {
			TileInterdictionMatrix matrix = container.getSafeHost();
			if (matrix != null) {
				FluidTank tank = new FluidTank(matrix.fortronCapacity.get());
				tank.setFluid(new FluidStack(ModularForcefieldsFluids.FLUID_FORTRON, matrix.fortron.get()));
				return tank;
			}
			return null;
		}, 8, 60));
		addComponent(new ScreenComponentMultiLabel(0, 0, matrixStack -> {
			if (menu.getUnsafeHost() instanceof TileInterdictionMatrix matrix) {
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.transfer", ChatFormatter.getChatDisplayShort(matrix.getFortronUse() / 1000 * 20, DisplayUnit.BUCKETS).append(" / s")), 25, 105, 4210752, false);
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.linked", matrix.getConnections()), 25, 95, 4210752, false);
				matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.frequency", matrix.getFrequency()), 25, 85, 4210752, false);
			}
		}));
		imageHeight += 51;
		inventoryLabelY += 51;
	}
}