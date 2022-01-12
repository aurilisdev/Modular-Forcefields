package modularforcefields.common.tile;

import java.util.HashSet;

import org.apache.commons.compress.utils.Sets;

import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronCapacitor extends GenericTile {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity);
	public static final int BASEENERGY = 700;
	public int fortron;
	public int fortronCapacity;

	public TileFortronCapacitor(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_FORTRONCAPACITOR.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentTickable().tickServer(this::tickServer).tickCommon(this::tickCommon));
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::writeGuiPacket).guiPacketReader(this::readGuiPacket));
		addComponent(new ComponentInventory(this).size(4)
				.valid((index, stack, inv) -> VALIDMODULES.contains(DeferredRegisters.ITEMSUBTYPE_MAPPINGS.getOrDefault(stack.getItem(), null))));
		addComponent(new ComponentContainerProvider("container.fortroncapacitor")
				.createMenu((id, player) -> new ContainerFortronCapacitor(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
	}

	private void tickCommon(ComponentTickable tickable) {
	}

	private void tickServer(ComponentTickable tickable) {
		ComponentPacketHandler packets = getComponent(ComponentType.PacketHandler);
		if (tickable.getTicks() % 20 == 0) {
			int max = getMaxStored();
			fortron = Mth.clamp(fortron, 0, max);
			fortronCapacity = max;
			packets.sendGuiPacketToTracking();
		}
	}

	private int getMaxStored() {
		return (int) (BASEENERGY + BASEENERGY * 10 * Math.pow(1.051, getModuleCount(SubtypeModule.upgradespeed))
				+ BASEENERGY * 30 * Math.pow(1.051, getModuleCount(SubtypeModule.upgradecapacity) * 2.0));
	}

	private int getModuleCount(SubtypeModule module) {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		return inv.countItem(DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(module));
	}

	private void writeGuiPacket(CompoundTag compound) {
		compound.putInt("fortron", fortron);
		compound.putInt("fortronCapacity", fortronCapacity);
	}

	private void readGuiPacket(CompoundTag compound) {
		fortron = compound.getInt("fortron");
		fortronCapacity = compound.getInt("fortronCapacity");
	}
}
