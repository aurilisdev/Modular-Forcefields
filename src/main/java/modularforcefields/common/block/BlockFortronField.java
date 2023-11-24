package modularforcefields.common.block;

import java.util.List;

import electrodynamics.prefab.block.GenericEntityBlock;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.tile.FortronFieldStatus;
import modularforcefields.common.tile.TileFortronField;
import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFortronField extends GenericEntityBlock {

	public BlockFortronField() {
		super(BlockBehaviour.Properties.of(Material.STONE).color(MaterialColor.STONE).strength(-1.0F, 3600000.0F).noOcclusion());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		if (getter instanceof Level level) {
			float bound = level.isClientSide() ? 0.01f : 0.0625F;
			List<Player> players = level.getEntities(EntityTypeTest.forClass(Player.class), new AABB(pos.getX() - bound, pos.getY() - bound, pos.getZ() - bound, pos.getX() + 1 + bound, pos.getY() + 1 + bound, pos.getZ() + 1 + bound), t -> true);
			for (Player player : players) {
				if (player.isCreative()) {
					return Shapes.empty();
				}
			}
			return Shapes.box(bound, bound, bound, 1 - bound, 1 - bound, 1 - bound);
		}
		return super.getCollisionShape(state, getter, pos, context);
	}

	@Override
	public void entityInside(BlockState state, Level lvl, BlockPos pos, Entity ent) {
		if (!lvl.isClientSide()) {
			if (ent instanceof LivingEntity living) {
				if (lvl.getBlockEntity(pos) instanceof TileFortronField field) {
					if (field.getProjectorPos() != null && lvl.getBlockEntity(field.getProjectorPos()) instanceof TileFortronFieldProjector projector) {
						int count = projector.countModules(SubtypeModule.upgradeshock);
						if (count > 0) {
							living.hurt(DamageSource.MAGIC, count);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (level.getBlockEntity(pos) instanceof TileFortronField field) {
			BlockPos projectorPos = field.getProjectorPos();
			if (projectorPos != null && level.getBlockEntity(projectorPos) instanceof TileFortronFieldProjector projector) {
				if (projector.getStatus() != FortronFieldStatus.DESTROYING) {
					return false;
				}
			}
		}
		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lvl, BlockState state, BlockEntityType<T> type) {
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileFortronField(pos, state);
	}
}
