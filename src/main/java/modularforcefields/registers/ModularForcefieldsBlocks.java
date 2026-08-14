package modularforcefields.registers;

import modularforcefields.ModularForcefields;
import modularforcefields.common.block.BlockFortronField;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import voltaic.api.registration.BulkRegistryObject;
import voltaic.common.block.BlockMachine;

public class ModularForcefieldsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModularForcefields.ID);

    public static final BulkRegistryObject<BlockMachine, SubtypeMFFSMachine> BLOCKS_MFFSMACHINE = new BulkRegistryObject<>(
	    SubtypeMFFSMachine.values(), subtype -> BLOCKS.register(subtype.tag(), () -> new BlockMachine(subtype) {
		@Override
		public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hitResult) {
		    ItemStack stack = player.getItemInHand(hand);
		    if (level.getBlockEntity(pos) instanceof TileFortronFieldProjector proj && proj.shouldColor) {
			for (DyeColor col : DyeColor.values()) {
			    if (stack.is(col.getTag())) {
				if (proj.fieldColorOrdinal.getValue() != col.ordinal()) {
				    proj.destroyField();
				}
				proj.fieldColorOrdinal.setValue(col.ordinal());
				break;
			    }
			}
		    }
		    return super.use(state, level, pos, player, hand, hitResult);
		}
	    }));
    public static final RegistryObject<BlockFortronField> BLOCK_FORTRONFIELD = BLOCKS.register("fortronfield",
	    BlockFortronField::new);

}
