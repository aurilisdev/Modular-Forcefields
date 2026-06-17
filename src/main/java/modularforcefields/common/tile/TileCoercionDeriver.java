package modularforcefields.common.tile;

import java.util.HashSet;
import java.util.function.Predicate;

import com.google.common.collect.Sets;

import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.settings.MFFSConstants;
import modularforcefields.registers.ModularForcefieldsItems;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentForgeEnergy;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.object.TransferPack;

public class TileCoercionDeriver extends TileFortronConnective {
    public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.upgradespeed,
	    SubtypeModule.upgradecapacity);
    public static final int BASEENERGY = 50;
    public SingleProperty<Integer> fortron = property(new SingleProperty<>(PropertyTypes.INTEGER, "fortron", 0));
    public SingleProperty<Integer> fortronCapacity = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "fortronCapacity", 0));

    public TileCoercionDeriver(BlockPos pos, BlockState state) {
	super(ModularForcefieldsTiles.TILE_COERCIONDERIVER.get(), pos, state);
	addComponent(new ComponentPacketHandler(this));
	addComponent(new ComponentElectrodynamic(this, true, true).voltage(MFFSConstants.COERCIONDERIVER_VOLTAGE)
		.setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM));
	addComponent(new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().forceSize(4))
		.valid((index, stack, inv) -> ModularForcefieldsItems.ITEMS_MODULE
			.getSpecificValues(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity)
			.contains(stack.getItem())));
	addComponent(new ComponentContainerProvider("coercionderiver", this)
		.createMenu((id, player) -> new ContainerCoercionDeriver(id, player,
			getComponent(IComponentType.Inventory), getCoordsArray())));
	addComponent(new ComponentForgeEnergy(this));
    }

    @Override
    protected void tickServer(ComponentTickable tickable) {
	super.tickServer(tickable);
	if (tickable.getTicks() % 20 == 0) {
	    onInventoryChange(getComponent(IComponentType.Inventory), 0);
	    boolean isLit = getBlockState().getValue(VoltaicBlockStates.LIT);
	    boolean shouldLit = fortron.getValue() > 0;
	    if (isLit != shouldLit) {
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(VoltaicBlockStates.LIT, shouldLit));
	    }
	}
	ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
	fortron.setValue((int) (fortron.getValue() + electro.extractPower(TransferPack.joulesVoltage(
		Math.min(getTransfer(), fortronCapacity.getValue() - fortron.getValue()), electro.getVoltage()), false)
		.getJoules()));
	fortron.setValue(
		fortron.getValue() - sendFortronTo(Math.min(fortron.getValue(), getTransfer()), getConnectionTest()));
    }

    @Override
    public void onInventoryChange(ComponentInventory inv, int slot) {
	super.onInventoryChange(inv, slot);
	int max = getMaxStored();
	ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
	electro.maxJoules(max);
	fortron.setValue(Mth.clamp(fortron.getValue(), 0, max));
	fortronCapacity.setValue(max);
    }

    private int getMaxStored() {
	return (int) (getTransfer() * 20 + BASEENERGY * countModules(SubtypeModule.upgradecapacity) * 40.0);
    }

    public int getTransfer() {
	return BASEENERGY * 30 + BASEENERGY * countModules(SubtypeModule.upgradespeed);
    }

    @Override
    protected Predicate<BlockEntity> getConnectionTest() {
	return TileFortronCapacitor.class::isInstance;
    }
}
