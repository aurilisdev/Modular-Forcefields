package modularforcefields.common.block;

import modularforcefields.common.tile.TileFortronField;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlockColorFortronField implements BlockColor {

	@Override
	public int getColor(BlockState state, BlockAndTintGetter getter, BlockPos pos, int tint) {
		if (getter.getBlockEntity(pos) instanceof TileFortronField field) {
			return field.getFieldColor().getColor().col;
		}
		return FortronFieldColor.LIGHT_BLUE.getColor().col;
	}
}
