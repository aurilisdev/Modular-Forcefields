package modularforcefields.client.screen;

import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.ScreenComponentFluid;
import electrodynamics.prefab.screen.component.ScreenComponentInfo;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.tile.TileCoercionDeriver;
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
		components.add(new ScreenComponentFluid(() -> {
			TileCoercionDeriver deriver = container.getHostFromIntArray();
			if (deriver != null) {
				FluidTank tank = new FluidTank(deriver.fortronCapacity);
				tank.setFluid(new FluidStack(DeferredRegisters.fluidFortron, deriver.fortron));
				return tank;
			}
			return null;
		}, this, 8, 27));
		components.add(new ScreenComponentElectricInfo(this, -ScreenComponentInfo.SIZE + 1, 2)
				.wattage(electro -> electro.getHolder() instanceof TileCoercionDeriver deriver ? (double) deriver.fortron : 0));
		imageHeight += 40;
		inventoryLabelY += 40;
	}
}