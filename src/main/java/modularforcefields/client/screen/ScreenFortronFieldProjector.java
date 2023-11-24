package modularforcefields.client.screen;

import java.util.List;
import java.util.Map.Entry;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.ScreenComponentMultiLabel;
import electrodynamics.prefab.screen.component.types.ScreenComponentSlot;
import electrodynamics.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
public class ScreenFortronFieldProjector extends GenericScreen<ContainerFortronFieldProjector> {
	public ScreenFortronFieldProjector(ContainerFortronFieldProjector container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addComponent(new ScreenComponentFluidGauge(() -> {
			TileFortronFieldProjector projector = container.getHostFromIntArray();
			if (projector != null) {
				FluidTank tank = new FluidTank(projector.fortronCapacity.get().intValue());
				tank.setFluid(new FluidStack(ModularForcefieldsFluids.fluidFortron, projector.fortron.get().intValue()));
				return tank;
			}
			return null;
		}, 8, 77));
		addComponent(new ScreenComponentMultiLabel(0, 0, matrixStack -> {
			if (menu.getUnsafeHost() instanceof TileFortronFieldProjector projector) {
				font.draw(matrixStack, MFFSTextUtils.gui("fortrondevice.linked", projector.getConnections()), 25, 115, 4210752);
				font.draw(matrixStack, MFFSTextUtils.gui("fortrondevice.usage", ChatFormatter.getChatDisplayShort(projector.getFortronUse() / 1000 * 20, DisplayUnit.BUCKETS)).append(" / s"), 25, 105, 4210752);
				font.draw(matrixStack, MFFSTextUtils.gui("fortrondevice.frequency", projector.getFrequency()), 25, 95, 4210752);
				font.draw(matrixStack, MFFSTextUtils.gui("fieldprojector.status", projector.getStatus().name()), 8, 130, 4210752);
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