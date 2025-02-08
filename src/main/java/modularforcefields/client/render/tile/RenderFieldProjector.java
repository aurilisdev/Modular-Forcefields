package modularforcefields.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.client.render.tile.AbstractTileRenderer;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.RenderingUtils;
import electrodynamics.prefab.utilities.math.MathUtils;
import modularforcefields.client.ClientRegister;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.common.tile.projection.ProjectionType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;

public class RenderFieldProjector extends AbstractTileRenderer<TileFortronFieldProjector> {

    public RenderFieldProjector(BlockEntityRendererProvider.Context context) {
	super(context);
    }

    @Override
    public void render(TileFortronFieldProjector tile, float partialTicks, PoseStack poseStack,
	    MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
	double ticks = tile.<ComponentTickable>getComponent(IComponentType.Tickable).getTicks();
	float base = (float) ((ticks + partialTicks) * 1 % 360);
	float rotX = base;
	float rotY = base;
	float rotZ = base;
	BakedModel ibakedmodel = null;
	if (tile.fortron.get() > 0) {
	    poseStack.pushPose();
	    poseStack.translate(0.5, 0.5, 0.5);
	    RenderingUtils.renderModel(Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_FIELDFORTRON), tile, RenderType.translucent(), poseStack, bufferIn,
		    combinedLightIn, combinedOverlayIn);
	    poseStack.popPose();
	    switch (ProjectionType.values()[tile.typeOrdinal.get()]) {
	    case HEMISPHERE:
		ibakedmodel = Minecraft.getInstance().getModelManager()
			.getModel(ClientRegister.MODEL_PREVIEWHALFSPHERE);
		break;
	    case PYRAMID:
		ibakedmodel = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_PREVIEWPYRAMID);
		break;
	    case SPHERE:
		ibakedmodel = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_PREVIEWSPHERE);
		break;
	    case CUBE:
		ibakedmodel = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_PREVIEWCUBE);
		break;
	    default:
	    case NONE:
		break;
	    }
	}
	if (ibakedmodel != null) {
	    poseStack.translate(0.5, 1.25, 0.5);
	    poseStack.scale(0.25f, 0.25f, 0.25f);
	    poseStack.mulPose(MathUtils.rotQuaternionDeg(rotX, rotY, rotZ));
	    RenderingUtils.renderModel(ibakedmodel, tile, RenderType.translucent(), poseStack, bufferIn,
		    combinedLightIn, combinedOverlayIn);
	}
    }

}
