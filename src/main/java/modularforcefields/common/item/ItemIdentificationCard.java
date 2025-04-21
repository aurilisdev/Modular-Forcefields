package modularforcefields.common.item;

import java.util.List;

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
import voltaic.common.item.ItemVoltaic;

public class ItemIdentificationCard extends ItemVoltaic {

	public ItemIdentificationCard(Properties pProperties) {
		super(pProperties, ModularForcefieldsCreativeTabs.MAIN);
	}

	public void onUsage(Player player, ItemStack stack) {
		stack.set(ModularForcefieldsDataComponentTypes.UUID, player.getUUID());
		stack.set(ModularForcefieldsDataComponentTypes.NAME, player.getName().getString());
		player.displayClientMessage(MFFSTextUtils.chatMessage("identificationcard.text", player.getName()), true);
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
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		if (stack.has(ModularForcefieldsDataComponentTypes.NAME)) {
			tooltipComponents.add(MFFSTextUtils.chatMessage("identificationcard.id", stack.get(ModularForcefieldsDataComponentTypes.NAME)));
		}
	}
}
