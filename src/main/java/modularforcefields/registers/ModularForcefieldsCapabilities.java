package modularforcefields.registers;

import modularforcefields.ModularForcefields;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import voltaic.prefab.tile.GenericTile;
import voltaic.registers.VoltaicCapabilities;

@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD)
public class ModularForcefieldsCapabilities {

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {

	/* TILES */

	ModularForcefieldsTiles.BLOCK_ENTITY_TYPES.getEntries().forEach(entry -> {
	    event.registerBlockEntity(VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK,
		    (BlockEntityType<? extends GenericTile>) entry.get(),
		    GenericTile::getElectrodynamicCapability);
	    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
		    (BlockEntityType<? extends GenericTile>) entry.get(),
		    GenericTile::getFluidHandlerCapability);
	    event.registerBlockEntity(VoltaicCapabilities.CAPABILITY_GASHANDLER_BLOCK,
		    (BlockEntityType<? extends GenericTile>) entry.get(),
		    GenericTile::getGasHandlerCapability);
	    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
		    (BlockEntityType<? extends GenericTile>) entry.get(),
		    GenericTile::getItemHandlerCapability);
	    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
		    (BlockEntityType<? extends GenericTile>) entry.get(),
		    GenericTile::getForgeEnergyCapability);
	});

    }

}
