package modularforcefields.client.screen;

import java.util.List;
import java.util.Map.Entry;

import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentMultiLabel;
import voltaic.prefab.screen.component.types.ScreenComponentSlot;
import voltaic.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;

public class ScreenFortronFieldProjector extends GenericScreen<ContainerFortronFieldProjector> {
    public ScreenFortronFieldProjector(ContainerFortronFieldProjector container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        addComponent(new ScreenComponentFluidGauge(() -> {
            TileFortronFieldProjector projector = container.getSafeHost();
            if (projector != null) {
                FluidTank tank = new FluidTank(projector.fortronCapacity.getValue().intValue());
                tank.setFluid(new FluidStack(ModularForcefieldsFluids.FLUID_FORTRON.get(), projector.fortron.getValue().intValue()));
                return tank;
            }
            return null;
        }, 8, 77));
        addComponent(new ScreenComponentMultiLabel(0, 0, poseStack -> {
            if (menu.getUnsafeHost() instanceof TileFortronFieldProjector projector) {
            	font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.linked", projector.getConnections()), 25, 115, 4210752);
            	font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.usage", ChatFormatter.getChatDisplayShort((int) (projector.getFortronUse() / 1000.0 * 20), DisplayUnits.BUCKETS)).append(" / s"), 25, 105, 4210752);
            	font.draw(poseStack, MFFSTextUtils.gui("fortrondevice.frequency", projector.getFrequency()), 25, 95, 4210752);
            	font.draw(poseStack, MFFSTextUtils.gui("fieldprojector.status", projector.getStatus().name()), 8, 130, 4210752);
            }
        }));
        imageHeight += 71;
        inventoryLabelY += 71;
    }

    @Override
    protected ScreenComponentSlot createScreenSlot(Slot slot) {
        ScreenComponentSlot component = super.createScreenSlot(slot);
        for (Entry<List<Integer>, String> ent : ContainerFortronFieldProjector.SLOT_MAP.entrySet()) {
            if (ent.getKey().contains(slot.index)) {
                component.tooltip(() -> slot.getItem().isEmpty() ? Component.literal(ent.getValue()) : slot.getItem().getHoverName());
            }
        }
        return component;
    }
}