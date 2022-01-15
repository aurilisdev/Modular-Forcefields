package modularforcefields.common.tile;

import java.util.HashSet;
import java.util.function.Predicate;

import org.apache.commons.compress.utils.Sets;

import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.object.TransferPack;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.settings.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileCoercionDeriver extends TileFortronConnective {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity);
	public static final int BASEENERGY = 700;
	public int fortron;
	public int fortronCapacity;

	public TileCoercionDeriver(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_COERCIONDERIVER.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::writeGuiPacket).guiPacketReader(this::readGuiPacket));
		addComponent(new ComponentElectrodynamic(this).voltage(Constants.COERCIONDERIVER_VOLTAGE).input(Direction.DOWN).output(Direction.DOWN));
		addComponent(new ComponentInventory(this).size(4)
				.valid((index, stack, inv) -> VALIDMODULES.contains(DeferredRegisters.ITEMSUBTYPE_MAPPINGS.getOrDefault(stack.getItem(), null))));
		addComponent(new ComponentContainerProvider("container.coercionderiver")
				.createMenu((id, player) -> new ContainerCoercionDeriver(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
	}

	@Override
	protected void tickServer(ComponentTickable tickable) {
		ComponentElectrodynamic electro = getComponent(ComponentType.Electrodynamic);
		ComponentPacketHandler packets = getComponent(ComponentType.PacketHandler);
		if (tickable.getTicks() % 20 == 0) {
			int max = getMaxStored();
			electro.maxJoules(max);
			fortron = Mth.clamp(fortron, 0, max);
			fortronCapacity = max;
			packets.sendGuiPacketToTracking();
		}
		fortron += electro.extractPower(TransferPack.joulesVoltage(Math.min(getTransfer(), fortronCapacity - fortron), electro.getVoltage()), false)
				.getJoules();
		sendFortronTo(fortron, getConnectionTest());
	}

	private int getMaxStored() {
		return (int) (BASEENERGY + BASEENERGY * 10 * Math.pow(1.051, getModuleCount(SubtypeModule.upgradespeed))
				+ BASEENERGY * 30 * Math.pow(1.051, getModuleCount(SubtypeModule.upgradecapacity) * 2.0));
	}

	private int getTransfer() {
		return (int) (BASEENERGY + BASEENERGY * 300 * Math.pow(1.051, getModuleCount(SubtypeModule.upgradespeed))) / 3;
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
	protected Predicate<BlockEntity> getConnectionTest() {
		return b -> b.getType() == DeferredRegisters.TILE_FORTRONCAPACITOR.get();
	}

}
