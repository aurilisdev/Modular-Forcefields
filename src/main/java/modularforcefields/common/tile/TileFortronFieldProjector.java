package modularforcefields.common.tile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.utils.Sets;

import electrodynamics.api.ISubtype;
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
import modularforcefields.common.tile.projection.ProjectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronFieldProjector extends TileFortronConnective {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
	public static final int BASEENERGY = 100;
	private FortronFieldColor fieldColor = FortronFieldColor.LIGHT_BLUE;
	private boolean isActivated = false;
	private boolean isCalculating = false;
	public Set<BlockPos> calculatedFieldPoints = Collections.synchronizedSet(new HashSet<>());
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

	public BlockPos getShiftedPos() {
		return worldPosition; // TODO: Implement actual shifted coordinates
	}

	public boolean isInterior() {
		return getModuleCount(SubtypeModule.upgradeinterior) > 0;
	}

	public ProjectionType getProjectionType() {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		ItemStack stack = inv.getItem(ContainerFortronFieldProjector.SLOT_TYPE);
		ISubtype subtype = DeferredRegisters.ITEMSUBTYPE_MAPPINGS.get(stack.getItem());
		if (subtype instanceof SubtypeModule module) {
			switch (module) {
			case shapecube:
				return ProjectionType.CUBE;
			case shapehemisphere:
				return ProjectionType.HEMISPHERE;
			case shapepyramid:
				return ProjectionType.PYRAMID;
			case shapesphere:
				return ProjectionType.SPHERE;
			default:
				break;
			}
		}
		return ProjectionType.NONE;
	}

	public FortronFieldColor getFieldColor() {
		return fieldColor;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setCalculating(boolean isCalculating) {
		this.isCalculating = isCalculating;
	}

	public boolean isCalculating() {
		return isCalculating;
	}

}
