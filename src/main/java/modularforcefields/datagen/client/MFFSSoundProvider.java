package modularforcefields.datagen.client;

import modularforcefields.ModularForcefields;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.datagen.utils.client.BaseSoundProvider;

public class MFFSSoundProvider extends BaseSoundProvider {

	public MFFSSoundProvider(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, helper, ModularForcefields.ID);
	}

	@Override
	public void registerSounds() {

	}

}
