package modularforcefields.client.screen;

import java.util.List;
import java.util.Map.Entry;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentFluid;
import electrodynamics.prefab.screen.component.ScreenComponentSlot;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.tile.TileFortronCapacitor;
import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
		components.add(new ScreenComponentFluid(() -> {
			TileFortronFieldProjector projector = container.getHostFromIntArray();
			if (projector != null) {
				FluidTank tank = new FluidTank(projector.fortronCapacity);
				tank.setFluid(new FluidStack(DeferredRegisters.fluidFortron, projector.fortron));
				return tank;
			}
			return null;
		}, this, 8, 77));
		imageHeight += 71;
		inventoryLabelY += 71;
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		super.renderLabels(matrixStack, mouseX, mouseY);
		if (menu.getUnsafeHost() instanceof TileFortronCapacitor capacitor) {
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.transfer", ChatFormatter.getChatDisplayShort(capacitor.getTransfer(), DisplayUnit.BUCKETS)), 25, 65, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.linked", capacitor.getConnections()), 25, 55, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.usage", ChatFormatter.getChatDisplayShort(capacitor.getTransfer() * 20, DisplayUnit.WATT)), 25, 45, 4210752);
			font.draw(matrixStack, new TranslatableComponent("gui.fortrondevice.frequency", capacitor.getFrequency()), 25, 35, 4210752);
		}
	}

	@Override
	protected ScreenComponentSlot createScreenSlot(Slot slot) {
		ScreenComponentSlot component = super.createScreenSlot(slot);
		for (Entry<List<Integer>, String> ent : ContainerFortronFieldProjector.SLOT_MAP.entrySet()) {
			if (ent.getKey().contains(slot.index)) {
				component.tooltip(() -> slot.getItem().isEmpty() ? new TextComponent(ent.getValue()) : slot.getItem().getHoverName());
			}
		}
		return component;
	}
}