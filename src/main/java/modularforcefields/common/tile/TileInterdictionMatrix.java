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
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class TileInterdictionMatrix extends TileFortronConnective {
	public static final int BASEENERGY = 100;
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
	public int fortronCapacity;
	public int fortron;
	public int radius;
	public int frequency;
	private int scaleEnergy;

	public TileInterdictionMatrix(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_INTERDICTIONMATRIX.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentTickable().tickServer(this::tickServer).tickCommon(this::tickCommon));
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::saveAdditional).guiPacketReader(this::load));
		addComponent(new ComponentInventory(this).size(18).shouldSendInfo().valid((index, stack, inv) -> true).onChanged(this::onChanged));
		addComponent(new ComponentContainerProvider("container.interdictionmatrix").createMenu((id, player) -> new ContainerInterdictionMatrix(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
	}

	@Override
	protected void tickCommon(ComponentTickable tickable) {
		super.tickCommon(tickable);
		if (tickable.getTicks() % 20 == 0) {
			fortronCapacity = getMaxFortron();
			fortron = Mth.clamp(fortron, 0, fortronCapacity);
		}
	}

	@Override
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	@Override
	public int getFrequency() {
		return frequency;
	}

	@Override
	protected void tickServer(ComponentTickable tickable) {
		super.tickServer(tickable);
	}

	public int getMaxFortron() {
		return getFortronUse() * 40 + BASEENERGY;
	}

	public int getFortronUse() {
		return scaleEnergy * -1;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("frequency", frequency);
		tag.putInt("fortronCapacity", fortronCapacity);
		tag.putInt("fortron", fortron);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		frequency = tag.getInt("frequency");
		fortronCapacity = tag.getInt("fortronCapacity");
		fortron = tag.getInt("fortron");
	}

	@Override
	protected boolean canRecieveFortron(TileFortronConnective tile) {
		return tile instanceof TileFortronCapacitor;
	}

	private void onChanged(ComponentInventory inv) {
		radius = countModules(SubtypeModule.manipulationscale, 0, 11);
		scaleEnergy = BASEENERGY * radius * radius * radius;
	}
}
