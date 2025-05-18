package modularforcefields.client.screen;

import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.tile.TileFortronCapacitor;
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

public class ScreenFortronCapacitor extends GenericScreen<ContainerFortronCapacitor> {
    public ScreenFortronCapacitor(ContainerFortronCapacitor container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        addComponent(new ScreenComponentFluidGauge(() -> {
            TileFortronCapacitor capacitor = container.getSafeHost();
            if (capacitor != null) {
                FluidTank tank = new FluidTank(capacitor.fortronCapacity.getValue().intValue());
                tank.setFluid(new FluidStack(ModularForcefieldsFluids.FLUID_FORTRON.get(), capacitor.fortron.getValue().intValue()));
                return tank;
            }
            return null;
        }, 8, 27));
        addComponent(new ScreenComponentMultiLabel(0, 0, poseStack -> {
        	
        	TileFortronCapacitor capacitor = menu.getSafeHost();
        	
        	if(capacitor == null) {
        		return;
        	}
        	
        	font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.transfer", ChatFormatter.getChatDisplayShort((int) (capacitor.getTransfer() / 1000.0 * 20), DisplayUnits.BUCKETS).append(" / s")), 25, 45, 4210752);
        	font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.linked", capacitor.getConnections()), 25, 55, 4210752);
        	font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.frequency", capacitor.getFrequency()), 25, 35, 4210752);
        }));
        imageHeight += 40;
        inventoryLabelY += 40;
    }
}