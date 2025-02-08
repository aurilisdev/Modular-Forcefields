package modularforcefields.datagen.client;

import electrodynamics.datagen.client.ElectrodynamicsLangKeyProvider;
import modularforcefields.References;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.ModularForcefieldsFluids;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.data.PackOutput;

public class MFFSLangKeyProvider extends ElectrodynamicsLangKeyProvider {

	public MFFSLangKeyProvider(PackOutput output, Locale locale) {
		super(output, locale, References.ID);
	}

	@Override
	protected void addTranslations() {

		switch (locale) {
		case EN_US:
		default:

			addCreativeTab("main", "Modular Forcefields");

			addItem(ModularForcefieldsItems.ITEM_FOCUSMATRIX, "Focus Matrix");
			addItem(ModularForcefieldsItems.ITEM_FREQUENCYCARD, "Fortron Frequency Card");
			addItem(ModularForcefieldsItems.ITEM_IDENTIFICATIONCARD, "Identification Card");

			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.manipulationscale), "Scale Module");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.manipulationtranslate), "Translation Module");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.shapecube), "Cube Shape Module");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.shapehemisphere), "Hemisphare Shape Module");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.shapepyramid), "Pyramid Shape Module");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.shapesphere), "Sphere Shape Module");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeantifriendly), "Anti-Friendly Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeantihostile), "Anti-Hostile Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeantipersonnel), "Anti-Personell Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeantispawn), "Anti-Spawn Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeblockaccess), "Block-Access Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeblockalter), "Block-Alter Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradecapacity), "Capacity Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradecollection), "Collection Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradecolorchange), "Color Change Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeconfiscate), "Confiscation Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradedisintegration), "Disintegration Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeinterior), "Interior Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradeshock), "Shock Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradespeed), "Speed Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradesponge), "Sponge Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradestabilize), "Stabilization Upgrade");
			addItem(ModularForcefieldsItems.ITEMS_MODULE.getValue(SubtypeModule.upgradestrength), "Strength Upgrade");

			addBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.biometricidentifier), "Biometric Identifier");
			addBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver), "Coercion Deriver");
			addBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortroncapacitor), "Fortron Capacitor");
			addBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortronfieldprojector), "Fortron Field Projector");
			addBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.interdictionmatrix), "Interdiction Matrix");

			addBlock(ModularForcefieldsBlocks.BLOCK_FORTRONFIELD, "Fortron Field");

			addFluid(ModularForcefieldsFluids.FLUID_FORTRON, "Fortron Fluid");

			addContainer("coercionderiver", "Coercion Deriver");
			addContainer("fortroncapacitor", "Fortron Capacitor");
			addContainer("fortronfieldprojector", "Fortron Field Projector");
			addContainer("interdictionmatrix", "Interdiction Matrix");
			addContainer("biometricidentifier", "Biometric Identifier");

			addTooltip("coercionderiver", "Produces Fortron");
			addTooltip("fortroncapacitor", "Buffers Fortron");
			addTooltip("fortronfieldprojector", "Projects Fortron Field");
			addTooltip("interdictionmatrix", "Enforces rules");
			addTooltip("biometricidentifier", "Stores players");
			
			addGuiLabel("fortrondevice.transfer", "Transfer: %s");
			addGuiLabel("fortrondevice.linked", "Linked Devices: %s");
			addGuiLabel("fortrondevice.usage", "Usage: %s");
			addGuiLabel("fortrondevice.frequency", "Frequency: %s");
			addGuiLabel("fieldprojector.status", "Status: %s");
			
			addChatMessage("identificationcard.text", "ID set to: %s");
			addChatMessage("identificationcard.id", "Name: %s");
			addChatMessage("frequencycard.text", "Set frequency to: %s");
			addChatMessage("frequencycard.freq", "Frequency: %s");

			addGuidebook(References.ID, "MFFS");

			addGuidebook("chapter.blocks", "Blocks");
			addGuidebook("chapter.modules", "Modules");
			addGuidebook("chapter.items", "Other Items");

		}

	}

}
