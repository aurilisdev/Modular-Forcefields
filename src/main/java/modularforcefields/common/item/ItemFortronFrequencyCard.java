package modularforcefields.common.item;

import java.util.List;

import modularforcefields.common.tile.TileFortronConnective;
import modularforcefields.prefab.utils.MFFSTextUtils;
import modularforcefields.registers.ModularForcefieldsCreativeTabs;
import modularforcefields.registers.ModularForcefieldsDataComponentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import voltaic.common.item.ItemVoltaic;

public class ItemFortronFrequencyCard extends ItemVoltaic {

    public ItemFortronFrequencyCard(Properties pProperties) {
	super(pProperties, ModularForcefieldsCreativeTabs.MAIN);
    }

    public void onUsage(Player player, ItemStack stack) {
	if (!player.level().isClientSide) {
	    int freq = stack.getOrDefault(ModularForcefieldsDataComponentTypes.FREQUENCY, 0);
	    if (player.isShiftKeyDown()) {
		freq--;
	    } else {
		freq++;
	    }
	    if (freq < 0) {
		freq = 20;
	    } else if (freq > 20) {
		freq = 0;
	    }
	    stack.set(ModularForcefieldsDataComponentTypes.FREQUENCY, freq);
	    player.displayClientMessage(MFFSTextUtils.chatMessage("frequencycard.text", freq), true);
	}
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
	BlockEntity entity = context.getLevel().getBlockEntity(context.getClickedPos());
	if (entity instanceof TileFortronConnective connective) {
	    connective.setFrequency(stack.getOrDefault(ModularForcefieldsDataComponentTypes.FREQUENCY, 0));
	}
	return super.onItemUseFirst(stack, context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
	onUsage(pPlayer, pPlayer.getItemInHand(pUsedHand));
	return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
	    TooltipFlag tooltipFlag) {
	super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	if (stack.has(ModularForcefieldsDataComponentTypes.FREQUENCY)) {
	    tooltipComponents.add(MFFSTextUtils.chatMessage("frequencycard.freq",
		    stack.get(ModularForcefieldsDataComponentTypes.FREQUENCY)));
	}

    }

}
