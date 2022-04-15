package modularforcefields.common.tile;

import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.commons.compress.utils.Sets;

import electrodynamics.api.ISubtype;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class TileFortronCapacitor extends TileFortronConnective {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity);
	public static final int BASEENERGY = 100;
	public int fortron;
	public int fortronCapacity;

	public TileFortronCapacitor(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_FORTRONCAPACITOR.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::writeGuiPacket).guiPacketReader(this::readGuiPacket));
		addComponent(new ComponentInventory(this).size(4).shouldSendInfo().valid((index, stack, inv) -> {
			for (Entry<ISubtype, RegistryObject<Item>> en : DeferredRegisters.SUBTYPEITEMREGISTER_MAPPINGS.entrySet()) {
				if (VALIDMODULES.contains(en.getKey())) {
					if (en.getValue().get() == stack.getItem()) {
						return true;
					}
				}
			}
			return false;

		}));
		addComponent(new ComponentContainerProvider("container.fortroncapacitor").createMenu((id, player) -> new ContainerFortronCapacitor(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
	}

	@Override
	protected void tickServer(ComponentTickable tickable) {
		super.tickServer(tickable);
		ComponentPacketHandler packets = getComponent(ComponentType.PacketHandler);
		if (tickable.getTicks() % 20 == 0) {
			int max = getMaxStored();
			fortron = Mth.clamp(fortron, 0, max);
			fortronCapacity = max;
			packets.sendGuiPacketToTracking();
		}
		fortron -= sendFortronTo(Math.min(fortron, getTransfer()), entity -> !(entity instanceof TileCoercionDeriver));
	}

	private int getMaxStored() {
		return (int) (getTransfer() + BASEENERGY * countModules(SubtypeModule.upgradecapacity) * 2.0);
	}

	public int getTransfer() {
		return BASEENERGY * 30 + BASEENERGY * countModules(SubtypeModule.upgradespeed);
	}

	private void writeGuiPacket(CompoundTag compound) {
		compound.putInt("fortron", fortron);
		compound.putInt("fortronCapacity", fortronCapacity);
	}

	private void readGuiPacket(CompoundTag compound) {
		fortron = compound.getInt("fortron");
		fortronCapacity = compound.getInt("fortronCapacity");
	}

	@Override
	protected boolean canRecieveFortron(TileFortronConnective tile) {
		return true;
	}

	@Override
	protected int recieveFortron(int amount) {
		int received = Math.max(0, Math.min(amount, fortronCapacity - fortron));
		fortron += received;
		return received;
	}
}
