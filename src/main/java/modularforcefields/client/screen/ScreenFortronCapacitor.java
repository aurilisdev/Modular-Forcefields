package modularforcefields.client.screen;

import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentFluid;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.tile.TileFortronCapacitor;
import net.minecraft.network.chat.Component;
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
}