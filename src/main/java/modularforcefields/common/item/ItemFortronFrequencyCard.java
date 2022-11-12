package modularforcefields.common.item;

import java.util.List;

import modularforcefields.common.tile.TileFortronConnective;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemFortronFrequencyCard extends Item {

	public ItemFortronFrequencyCard(Properties pProperties) {
		super(pProperties);
	}

	public void onUsage(Player player, ItemStack stack) {
		if (!player.level.isClientSide) {
			CompoundTag tag = stack.getOrCreateTag();
			int freq = tag.getInt("frequency");
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
			tag.putInt("frequency", freq);
			player.displayClientMessage(Component.translatable("message.frequencycard.text", freq), true);
		}
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		BlockEntity entity = context.getLevel().getBlockEntity(context.getClickedPos());
		if (entity instanceof TileFortronConnective connective) {
			connective.setFrequency(stack.getOrCreateTag().getInt("frequency"));
		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		onUsage(pPlayer, pPlayer.getItemInHand(pUsedHand));
		return super.use(pLevel, pPlayer, pUsedHand);
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		if (pStack.hasTag()) {
			pTooltipComponents.add(Component.translatable("message.frequencycard.freq", pStack.getOrCreateTag().getInt("frequency")));
		}
	}
}
