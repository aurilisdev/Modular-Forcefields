package modularforcefields.client.screen;

import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import modularforcefields.common.tile.TileInterdictionMatrix;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentMultiLabel;
import voltaic.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;

public class ScreenInterdictionMatrix extends GenericScreen<ContainerInterdictionMatrix> {
	public ScreenInterdictionMatrix(ContainerInterdictionMatrix container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addComponent(new ScreenComponentFluidGauge(() -> {
			TileInterdictionMatrix matrix = container.getSafeHost();
			if (matrix != null) {
				FluidTank tank = new FluidTank(matrix.fortronCapacity.getValue());
				tank.setFluid(new FluidStack(ModularForcefieldsFluids.FLUID_FORTRON.get(), matrix.fortron.getValue()));
				return tank;
			}
			return null;
		}, 8, 60));
		addComponent(new ScreenComponentMultiLabel(0, 0, poseStack -> {
			
			TileInterdictionMatrix matrix = menu.getSafeHost();
			
			if(matrix == null) {
				return;
			}
			
			font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.transfer", ChatFormatter.getChatDisplayShort((int)(matrix.getFortronUse() / 1000.0 * 20), DisplayUnits.BUCKETS).append(" / s")), 25, 105, 4210752);
			font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.linked", matrix.getConnections()), 25, 95, 4210752);
			font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.frequency", matrix.getFrequency()), 25, 85, 4210752);
		}));
		imageHeight += 51;
		inventoryLabelY += 51;
	}
}