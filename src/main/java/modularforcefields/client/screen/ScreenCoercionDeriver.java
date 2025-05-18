package modularforcefields.client.screen;

import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.tile.TileCoercionDeriver;
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
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;

public class ScreenCoercionDeriver extends GenericScreen<ContainerCoercionDeriver> {
    public ScreenCoercionDeriver(ContainerCoercionDeriver container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        addComponent(new ScreenComponentFluidGauge(() -> {
            TileCoercionDeriver deriver = container.getSafeHost();
            if (deriver != null) {
                FluidTank tank = new FluidTank(deriver.fortronCapacity.getValue());
                tank.setFluid(new FluidStack(ModularForcefieldsFluids.FLUID_FORTRON.get(), deriver.fortron.getValue()));
                return tank;
            }
            return null;
        }, 8, 27));
        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(electro -> electro.getHolder() instanceof TileCoercionDeriver deriver ? (double) deriver.fortron.getValue() : 0));
        addComponent(new ScreenComponentMultiLabel(0, 0, matrixStack -> {
            if (menu.getUnsafeHost() instanceof TileCoercionDeriver deriver) {
                matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.transfer", ChatFormatter.getChatDisplayShort((int) (deriver.getTransfer() / 1000.0 * 20), DisplayUnits.BUCKETS).append(" / s")), 25, 65, 4210752, false);
                matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.linked", deriver.getConnections()), 25, 55, 4210752, false);
                matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.usage", ChatFormatter.getChatDisplayShort(deriver.getTransfer() * 20, DisplayUnits.WATT)), 25, 45, 4210752, false);
                matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.frequency", deriver.getFrequency()), 25, 35, 4210752, false);
            }
        }));
        imageHeight += 40;
        inventoryLabelY += 40;
    }
}