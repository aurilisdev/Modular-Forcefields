package modularforcefields.datagen.client;

import modularforcefields.ModularForcefields;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.datagen.utils.client.BaseSoundProvider;

public class MFFSSoundProvider extends BaseSoundProvider {

	public MFFSSoundProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, helper, ModularForcefields.ID);
	}

	@Override
	public void registerSounds() {

	}

}
