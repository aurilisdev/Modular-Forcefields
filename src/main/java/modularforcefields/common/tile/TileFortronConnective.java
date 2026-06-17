package modularforcefields.common.tile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.WorldUtils;

public class TileFortronConnective extends GenericTile {

    protected HashSet<TileFortronConnective> connections = new HashSet<>();
    public SingleProperty<Integer> frequency = property(new SingleProperty<>(PropertyTypes.INTEGER, "frequency", 0));

    protected TileFortronConnective(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
	super(tileEntityTypeIn, worldPos, blockState);
	addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickCommon(this::tickCommon));
    }

    protected void tickCommon(ComponentTickable tickable) {
	long ticks = tickable.getTicks();
	if (ticks % 200 == 1) {
	    findConnections();
	}
	if (ticks % 20 == 0) {
	    validateConnections();
	}
    }

    protected void findConnections() {
	Predicate<BlockEntity> predicate = getConnectionTest();
	for (BlockEntity entity : WorldUtils.getNearbyTiles(level, worldPosition, 5)) {
	    if (entity != this && entity instanceof TileFortronConnective connection && predicate.test(entity)) {
		connections.add(connection);
		connection.connections.add(this);

	    }
	}
    }

    protected void validateConnections() {
	Iterator<TileFortronConnective> it = connections.iterator();
	while (it.hasNext()) {
	    TileFortronConnective connection = it.next();
	    if (getFrequency() != connection.getFrequency()) {
		connection.connections.remove(this);
		it.remove();
	    }
	}
    }

    protected void invalidateConnections() {
	Iterator<TileFortronConnective> it = connections.iterator();
	while (it.hasNext()) {
	    TileFortronConnective connection = it.next();
	    connection.connections.remove(this);
	    it.remove();
	}
    }

    protected int sendFortronTo(int send, Predicate<BlockEntity> valid) {
	int sent = 0;
	HashSet<TileFortronConnective> sendList = (HashSet<TileFortronConnective>) connections.clone();
	sendList.removeIf(connective -> !connective.canRecieveFortron(this) || !valid.test(connective));

	int size = sendList.size();
	for (TileFortronConnective connective : sendList) {
	    int ret = connective.recieveFortron(send / size);
	    sent += ret;
	    send -= ret;
	    size--;
	}
	return sent;
    }

    public int countModules(SubtypeModule module) {
	ComponentInventory inv = getComponent(IComponentType.Inventory);
	return inv.countItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(module));
    }

    public int countModules(SubtypeModule module, int... slots) {
	ComponentInventory inv = getComponent(IComponentType.Inventory);
	Item moduleItem = ModularForcefieldsItems.ITEMS_MODULE.getValue(module);
	int count = 0;
	for (int slot : slots) {
	    ItemStack itemstack = inv.getItem(slot);
	    if (itemstack.is(moduleItem)) {
		count += itemstack.getCount();
	    }
	}
	return count;
    }

    public boolean hasModule(SubtypeModule module) {
	ComponentInventory inv = getComponent(IComponentType.Inventory);
	Item moduleItem = ModularForcefieldsItems.ITEMS_MODULE.getValue(module);
	for (int slot = 0; slot < inv.getContainerSize(); slot++) {
	    ItemStack itemstack = inv.getItem(slot);
	    if (itemstack.is(moduleItem)) {
		return true;
	    }
	}
	return false;
    }

    protected void tickServer(ComponentTickable tickable) {
    }

    public int getFrequency() {
	return frequency.getValue();
    }

    public void setFrequency(int frequency) {
	this.frequency.setValue(frequency);
    }

    protected Predicate<BlockEntity> getConnectionTest() {
	return b -> true;
    }

    protected boolean canRecieveFortron(TileFortronConnective tile) {
	return false;
    }

    protected int recieveFortron(int amount) {
	return 0;
    }

    @Override
    public void setRemoved() {
	super.setRemoved();
	invalidateConnections();
    }

    public int getConnections() {
	return connections.size();
    }

}
