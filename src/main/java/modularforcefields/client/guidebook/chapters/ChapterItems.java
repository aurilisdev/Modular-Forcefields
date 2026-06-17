package modularforcefields.client.guidebook.chapters;

import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.network.chat.MutableComponent;
import voltaic.client.guidebook.utils.components.Chapter;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.AbstractGraphicWrapper;
import voltaic.client.guidebook.utils.pagedata.graphics.ItemWrapperObject;

public class ChapterItems extends Chapter {

    private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 32, 32, 32, 2.0F,
	    ModularForcefieldsItems.ITEM_IDENTIFICATIONCARD.get());

    public ChapterItems(Module module) {
	super(module);
    }

    @Override
    public AbstractGraphicWrapper<?> getLogo() {
	return LOGO;
    }

    @Override
    public MutableComponent getTitle() {
	return MFFSTextUtils.guidebook("chapter.items");
    }

    @Override
    public void addData() {

    }

}
