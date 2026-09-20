package modularforcefields.common.block;

import javax.annotation.Nullable;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlockColorFortronField implements BlockColor {

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tint) {
	return state.getValue(BlockFortronField.COLOR).getMapColor().col;
    }
}
