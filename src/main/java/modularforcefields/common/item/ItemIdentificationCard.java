package modularforcefields.common.item;

import java.util.List;

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

public class ItemIdentificationCard extends Item {

	public ItemIdentificationCard(Properties pProperties) {
		super(pProperties);
	}

	public void onUsage(Player player, ItemStack stack) {
		stack.getOrCreateTag().putUUID("player", player.getUUID());
		stack.getOrCreateTag().putString("name", player.getName().getString());
		player.displayClientMessage(Component.translatable("message.identificationcard.text", player.getName()), true);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		onUsage(pPlayer, pPlayer.getItemInHand(pUsedHand));
		return super.use(pLevel, pPlayer, pUsedHand);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		onUsage(pContext.getPlayer(), pContext.getItemInHand());
		return super.useOn(pContext);
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		if (pStack.hasTag()) {
			pTooltipComponents.add(Component.translatable("message.identificationcard.id", pStack.getOrCreateTag().getString("name")));
		}
	}
}
