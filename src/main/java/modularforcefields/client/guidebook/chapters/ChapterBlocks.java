package modularforcefields.client.guidebook.chapters;

import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.network.chat.MutableComponent;
import voltaic.client.guidebook.utils.components.Chapter;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.AbstractGraphicWrapper;
import voltaic.client.guidebook.utils.pagedata.graphics.ItemWrapperObject;

public class ChapterBlocks extends Chapter {

    private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 32, 32, 32, 2.0F, ModularForcefieldsItems.ITEMS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortronfieldprojector));

    public ChapterBlocks(Module module) {
        super(module);
    }

    @Override
    public AbstractGraphicWrapper<?> getLogo() {
        return LOGO;
    }

    @Override
    public MutableComponent getTitle() {
        return MFFSTextUtils.guidebook("chapter.blocks");
    }

    @Override
    public void addData() {

    }
}
