package modularforcefields.common.tile;

import java.util.HashSet;

import com.google.common.collect.Sets;

import electrodynamics.common.block.states.ElectrodynamicsBlockStates;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyTypes;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.registers.ModularForcefieldsItems;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileFortronCapacitor extends TileFortronConnective {
    public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.upgradespeed,
	    SubtypeModule.upgradecapacity);
    public static final int BASEENERGY = 100;
    public Property<Integer> fortron = property(new Property<>(PropertyTypes.INTEGER, "fortron", 0));
    public Property<Integer> fortronCapacity = property(new Property<>(PropertyTypes.INTEGER, "fortronCapacity", 0));

    public TileFortronCapacitor(BlockPos pos, BlockState state) {
	super(ModularForcefieldsTiles.TILE_FORTRONCAPACITOR.get(), pos, state);
	addComponent(new ComponentPacketHandler(this));
	addComponent(new ComponentInventory(this, InventoryBuilder.newInv().forceSize(4))
		.valid((index, stack, inv) -> ModularForcefieldsItems.ITEMS_MODULE
			.getSpecificValues(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity)
			.contains(stack.getItem())));
	addComponent(new ComponentContainerProvider("container.fortroncapacitor", this)
		.createMenu((id, player) -> new ContainerFortronCapacitor(id, player,
			getComponent(IComponentType.Inventory), getCoordsArray())));
    }

    @Override
    protected void tickServer(ComponentTickable tickable) {
	super.tickServer(tickable);
	if (tickable.getTicks() % 20 == 0) {
	    onInventoryChange(getComponent(IComponentType.Inventory), 0);
	    boolean isLit = getBlockState().getValue(ElectrodynamicsBlockStates.LIT);
	    boolean shouldLit = fortron.get() > 0;
	    if (isLit != shouldLit) {
		level.setBlockAndUpdate(worldPosition,
			getBlockState().setValue(ElectrodynamicsBlockStates.LIT, shouldLit));
	    }
	}
	fortron.set(fortron.get() - sendFortronTo(Math.min(fortron.get(), getTransfer()), this::canSendTo));
    }

    protected boolean canSendTo(BlockEntity entity) {
	if (entity instanceof TileCoercionDeriver) {
	    return false;
	}
	if (entity instanceof TileFortronCapacitor capacitor) {
	    for (TileFortronConnective connective : connections) {
		if (connective instanceof TileFortronFieldProjector projector) {
		    if (projector.activeFields.isEmpty()) {
			continue;
		    }
		    if (capacitor.connections.contains(projector)) {
			return false;
		    }
		}
	    }
	}
	return true;
    }

    @Override
    public void onInventoryChange(ComponentInventory inv, int slot) {
	super.onInventoryChange(inv, slot);
	int max = getMaxStored();
	fortron.set(Mth.clamp(fortron.get(), 0, max));
	fortronCapacity.set(max);
    }

    private int getMaxStored() {
	return (int) (getTransfer() * 20 + BASEENERGY * countModules(SubtypeModule.upgradecapacity) * 100.0);
    }

    public int getTransfer() {
	return BASEENERGY * 30 + BASEENERGY * countModules(SubtypeModule.upgradespeed);
    }

    @Override
    protected boolean canRecieveFortron(TileFortronConnective tile) {
	return true;
    }

    @Override
    protected int recieveFortron(int amount) {
	int received = Math.max(0, Math.min(amount, fortronCapacity.get() - fortron.get()));
	fortron.set(fortron.get() + received);
	return received;
    }
}
