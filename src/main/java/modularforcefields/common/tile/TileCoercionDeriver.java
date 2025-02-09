package modularforcefields.common.tile;

import java.util.HashSet;
import java.util.function.Predicate;

import com.google.common.collect.Sets;

import electrodynamics.common.block.states.ElectrodynamicsBlockStates;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyTypes;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import electrodynamics.prefab.utilities.object.TransferPack;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.settings.Constants;
import modularforcefields.registers.ModularForcefieldsItems;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileCoercionDeriver extends TileFortronConnective {
    public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity);
    public static final int BASEENERGY = 50;
    public Property<Integer> fortron = property(new Property<>(PropertyTypes.INTEGER, "fortron", 0));
    public Property<Integer> fortronCapacity = property(new Property<>(PropertyTypes.INTEGER, "fortronCapacity", 0));

    public TileCoercionDeriver(BlockPos pos, BlockState state) {
        super(ModularForcefieldsTiles.TILE_COERCIONDERIVER.get(), pos, state);
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentElectrodynamic(this, true, true).voltage(Constants.COERCIONDERIVER_VOLTAGE).setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM));
        addComponent(new ComponentInventory(this, InventoryBuilder.newInv().forceSize(4)).valid((index, stack, inv) -> ModularForcefieldsItems.ITEMS_MODULE.getSpecificValues(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity).contains(stack.getItem())));
        addComponent(new ComponentContainerProvider("container.coercionderiver", this).createMenu((id, player) -> new ContainerCoercionDeriver(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
    }

    @Override
    protected void tickServer(ComponentTickable tickable) {
        super.tickServer(tickable);
        if (tickable.getTicks() % 20 == 0) {
            onInventoryChange(getComponent(IComponentType.Inventory), 0);
            boolean isLit = getBlockState().getValue(ElectrodynamicsBlockStates.LIT);
            boolean shouldLit = fortron.get() > 0;
            if(isLit != shouldLit) {
        	level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ElectrodynamicsBlockStates.LIT, shouldLit));
            }
        }
        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
        fortron.set((int) (fortron.get() + electro.extractPower(TransferPack.joulesVoltage(Math.min(getTransfer(), fortronCapacity.get() - fortron.get()), electro.getVoltage()), false).getJoules()));
        fortron.set(fortron.get() - sendFortronTo(Math.min(fortron.get(), getTransfer()), getConnectionTest()));
    }

    @Override
    public void onInventoryChange(ComponentInventory inv, int slot) {
	super.onInventoryChange(inv, slot);
	int max = getMaxStored();
	ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
	electro.maxJoules(max);
	fortron.set(Mth.clamp(fortron.get(), 0, max));
	fortronCapacity.set(max);
    }

    private int getMaxStored() {
	return (int) (getTransfer() * 20 + BASEENERGY * countModules(SubtypeModule.upgradecapacity) * 40.0);
    }

    public int getTransfer() {
	return BASEENERGY * 30 + BASEENERGY * countModules(SubtypeModule.upgradespeed);
    }

    @Override
    protected Predicate<BlockEntity> getConnectionTest() {
	return b -> b instanceof TileFortronCapacitor;
    }
}
