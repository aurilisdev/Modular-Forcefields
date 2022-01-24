package modularforcefields.common.tile;

import java.util.HashSet;

import org.apache.commons.compress.utils.Sets;

import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.block.FortronFieldColor;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronFieldProjector extends TileFortronConnective {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
	public static final int BASEENERGY = 100;
	private FortronFieldColor fieldColor = FortronFieldColor.LIGHT_BLUE;
	private boolean isActivated = false;
	public int fortronCapacity;
	public int fortron;

	public TileFortronFieldProjector(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_FORTRONFIELDPROJECTOR.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::writeGuiPacket).guiPacketReader(this::readGuiPacket));
		addComponent(new ComponentInventory(this).size(21).shouldSendInfo().valid((index, stack, inv) -> true));
		addComponent(new ComponentContainerProvider("container.fortronfieldprojector").createMenu((id, player) -> new ContainerFortronFieldProjector(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
	}

	@Override
	protected void tickCommon(ComponentTickable tickable) {
	}

	@Override
	protected void tickServer(ComponentTickable tickable) {
	}

	private void writeGuiPacket(CompoundTag compound) {
	}

	private void readGuiPacket(CompoundTag compound) {
	}

	public FortronFieldColor getFieldColor() {
		return fieldColor;
	}

	public boolean isActivated() {
		return isActivated;
	}

}
