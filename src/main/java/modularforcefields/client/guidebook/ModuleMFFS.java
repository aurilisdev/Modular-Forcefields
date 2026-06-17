package modularforcefields.client.guidebook;

import modularforcefields.ModularForcefields;
import modularforcefields.client.guidebook.chapters.ChapterBlocks;
import modularforcefields.client.guidebook.chapters.ChapterItems;
import modularforcefields.client.guidebook.chapters.ChapterModules;
import modularforcefields.prefab.utils.MFFSTextUtils;
import net.minecraft.network.chat.MutableComponent;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.AbstractGraphicWrapper;
import voltaic.client.guidebook.utils.pagedata.graphics.ImageWrapperObject;

public class ModuleMFFS extends Module {

    private static final ImageWrapperObject LOGO = new ImageWrapperObject(0, 0, 0, 0, 32, 32, 32, 32,
	    ModularForcefields.rl("textures/screen/guidebook/modularforcefieldslogo.png"));

    @Override
    public AbstractGraphicWrapper<?> getLogo() {
	return LOGO;
    }

    @Override
    public MutableComponent getTitle() {
	return MFFSTextUtils.guidebook(ModularForcefields.ID);
    }

    @Override
    public void addChapters() {

	chapters.add(new ChapterBlocks(this));
	chapters.add(new ChapterModules(this));
	chapters.add(new ChapterItems(this));

    }

}
