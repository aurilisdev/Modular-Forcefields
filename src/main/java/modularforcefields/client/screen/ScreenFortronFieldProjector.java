package modularforcefields.client.screen;

import java.util.List;
import java.util.Map.Entry;

import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.settings.MFFSConstants;
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
    public ScreenFortronFieldProjector(ContainerFortronFieldProjector container, Inventory playerInventory,
	    Component title) {
	super(container, playerInventory, title);
	addComponent(new ScreenComponentFluidGauge(() -> {
	    TileFortronFieldProjector projector = container.getSafeHost();
	    if (projector != null) {
		FluidTank tank = new FluidTank(projector.fortronCapacity.getValue().intValue());
		tank.setFluid(new FluidStack(ModularForcefieldsFluids.FLUID_FORTRON.get(),
			projector.fortron.getValue().intValue()));
		return tank;
	    }
	    return null;
	}, 8, 77));
	addComponent(new ScreenComponentMultiLabel(0, 0, matrixStack -> {

	    TileFortronFieldProjector projector = menu.getSafeHost();

	    if (projector == null) {
		return;
	    }

	    matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.linked", projector.getConnections()), 25, 115,
		    4210752, false);
	    matrixStack.drawString(font,
		    MFFSTextUtils
			    .gui("fortrondevice.usage", ChatFormatter.getChatDisplayShort(
				    (int) (projector.getFortronUse() / 1000.0 * 20), DisplayUnits.BUCKETS))
			    .append(" / s"),
		    25, 105, 4210752, false);
	    matrixStack.drawString(font, MFFSTextUtils.gui("fortrondevice.frequency", projector.getFrequency()), 25, 95,
		    4210752, false);
	    matrixStack.drawString(font, MFFSTextUtils.gui("fieldprojector.status", projector.getStatus().name()), 8,
		    130, 4210752, false);
	    double maxHealth = MFFSConstants.FORTRONFIELD_MAXHEALTH;
	    double currentHealth = Math.max(0.0, Math.min(projector.health.getValue(), maxHealth));
	    double healthPercent = maxHealth <= 0.0 ? 0.0 : currentHealth / maxHealth;

	    int barX = 8;
	    int barY = 143;
	    int barWidth = 160;
	    int barHeight = 10;
	    int filledWidth = (int) Math.round(barWidth * healthPercent);

	    matrixStack.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF202020);

	    int healthColor = healthPercent > 0.5 ? 0xFF55FF55 : healthPercent > 0.25 ? 0xFFFFFF55 : 0xFFFF5555;
	    matrixStack.fill(barX, barY, barX + filledWidth, barY + barHeight, healthColor);

	    matrixStack.fill(barX, barY, barX + barWidth, barY + 1, 0xFF555555);
	    matrixStack.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, 0xFF555555);
	    matrixStack.fill(barX, barY, barX + 1, barY + barHeight, 0xFF555555);
	    matrixStack.fill(barX + barWidth - 1, barY, barX + barWidth, barY + barHeight, 0xFF555555);

	    matrixStack.drawString(font, Component.literal(String.format("%.0f / %.0f HP", currentHealth, maxHealth)),
		    25, 156, 4210752, false);

	}));
	imageHeight += 93;
	inventoryLabelY += 93;
    }

    @Override
    protected ScreenComponentSlot createScreenSlot(Slot slot) {
	ScreenComponentSlot component = super.createScreenSlot(slot);
	for (Entry<List<Integer>, String> ent : ContainerFortronFieldProjector.SLOT_MAP.entrySet()) {
	    if (ent.getKey().contains(slot.index)) {
		component.tooltip(() -> slot.getItem().isEmpty() ? Component.literal(ent.getValue())
			: slot.getItem().getHoverName());
	    }
	}
	return component;
    }
}