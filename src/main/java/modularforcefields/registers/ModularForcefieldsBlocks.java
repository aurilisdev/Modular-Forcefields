package modularforcefields.registers;

import modularforcefields.ModularForcefields;
import modularforcefields.common.block.BlockFortronField;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voltaic.api.registration.BulkDeferredHolder;
import voltaic.common.block.BlockMachine;

public class ModularForcefieldsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK,
	    ModularForcefields.ID);

    public static final BulkDeferredHolder<Block, BlockMachine, SubtypeMFFSMachine> BLOCKS_MFFSMACHINE = new BulkDeferredHolder<>(
	    SubtypeMFFSMachine.values(), subtype -> BLOCKS.register(subtype.tag(), () -> new BlockMachine(subtype) {
		@Override
		protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		    if (level.getBlockEntity(pos) instanceof TileFortronFieldProjector proj && proj.shouldColor) {
			for (DyeColor col : DyeColor.values()) {
			    if (stack.is(col.getTag())) {
				if (proj.fieldColorOrdinal.getValue() != col.ordinal()) {
				    proj.destroyField(false);
				}
				proj.fieldColorOrdinal.setValue(col.ordinal());
				break;
			    }
			}
		    }
		    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
		}
	    }));
    public static final DeferredHolder<Block, BlockFortronField> BLOCK_FORTRONFIELD = BLOCKS.register("fortronfield",
	    BlockFortronField::new);

}
