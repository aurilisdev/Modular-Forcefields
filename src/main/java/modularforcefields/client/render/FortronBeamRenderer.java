package modularforcefields.client.render;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import modularforcefields.ModularForcefields;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Renders fortron beams that remain active while refreshed and fade after their
 * duration expires.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ModularForcefields.ID, value = Dist.CLIENT)
public class FortronBeamRenderer {

    private static final int MAX_BEAMS = 512;
    private static final float WIDTH = 0.07F;
    private static final int FADE_TICKS = 14;
    private static final float FADE_STEP = 0.15F;
    private static final int BASE_ALPHA = 100;
    private static final int FULL_BRIGHT = 15728880;

    private static final List<Beam> BEAMS = new ArrayList<>();

    private static class Beam {
	private final Vec3 from;
	private final Vec3 to;
	private final int color;
	private int duration;
	private int age;
	private float shown;
	private float previousShown;

	private Beam(Vec3 from, Vec3 to, int color, int duration) {
	    this.from = from;
	    this.to = to;
	    this.color = color;
	    this.duration = Math.max(1, duration);
	}

	private boolean matches(Vec3 from, Vec3 to, int color) {
	    return this.color == color && ((this.from.equals(from) && this.to.equals(to))
		    || (this.from.equals(to) && this.to.equals(from)));
	}
    }

    public static void addBeam(Vec3 from, Vec3 to, int color, int duration) {
	Minecraft.getInstance().execute(() -> {
	    for (Beam beam : BEAMS) {
		if (beam.matches(from, to, color)) {
		    beam.age = 0;
		    beam.duration = Math.max(1, duration);
		    return;
		}
	    }
	    if (BEAMS.size() >= MAX_BEAMS) {
		int oldest = 0;
		for (int i = 1; i < BEAMS.size(); i++) {
		    if (BEAMS.get(i).age > BEAMS.get(oldest).age) {
			oldest = i;
		    }
		}
		BEAMS.remove(oldest);
	    }
	    BEAMS.add(new Beam(from, to, color, duration));
	});
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
	Minecraft mc = Minecraft.getInstance();
	if (mc.level == null) {
	    BEAMS.clear();
	    return;
	}
	if (mc.isPaused())
	    return;
	BEAMS.removeIf(beam -> {
	    beam.previousShown = beam.shown;
	    beam.age++;
	    if (beam.age <= beam.duration) {
		beam.shown = Math.min(1.0F, beam.shown + FADE_STEP);
	    } else {
		beam.shown = Math.max(0.0F, beam.shown - 1.0F / FADE_TICKS);
	    }
	    return beam.age > beam.duration && beam.shown <= 0.0F && beam.previousShown <= 0.0F;
	});
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
	if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || BEAMS.isEmpty())
	    return;
	Minecraft mc = Minecraft.getInstance();
	Vec3 cam = event.getCamera().getPosition();
	float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
	TextureAtlasSprite tintSprite = mc.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
		.getSprite(ModularForcefields.rl("block/fluid/fortroncolourless"));
	MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
	RenderType renderType = RenderType.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS);
	VertexConsumer consumer = buffers.getBuffer(renderType);
	PoseStack stack = event.getPoseStack();
	stack.pushPose();
	stack.translate(-cam.x, -cam.y, -cam.z);
	Matrix4f matrix = stack.last().pose();
	for (Beam beam : BEAMS) {
	    renderBeam(consumer, matrix, tintSprite, beam, partial);
	}
	stack.popPose();
	buffers.endBatch(renderType);
    }

    private static void renderBeam(VertexConsumer consumer, Matrix4f matrix, TextureAtlasSprite sprite, Beam beam,
	    float partial) {
	float opacity = beam.previousShown + (beam.shown - beam.previousShown) * partial;
	int alpha = (int) (BASE_ALPHA * opacity);
	if (alpha <= 0)
	    return;
	Vec3 dir = beam.to.subtract(beam.from);
	if (dir.lengthSqr() < 1.0E-4)
	    return;
	Vec3 axis = dir.normalize();
	Vec3 ref = Math.abs(axis.y) > 0.99 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
	Vec3 p1 = axis.cross(ref).normalize().scale(WIDTH);
	Vec3 p2 = axis.cross(p1).normalize().scale(WIDTH);
	int r = beam.color >> 16 & 0xFF;
	int g = beam.color >> 8 & 0xFF;
	int b = beam.color & 0xFF;
	double length = dir.length();
	int segments = Math.max(1, (int) Math.ceil(length));
	for (int i = 0; i < segments; i++) {
	    Vec3 start = beam.from.add(dir.scale((double) i / segments));
	    Vec3 end = beam.from.add(dir.scale((double) (i + 1) / segments));
	    quad(consumer, matrix, sprite, start, end, p1, r, g, b, alpha);
	    quad(consumer, matrix, sprite, start, end, p2, r, g, b, alpha);
	}
    }

    private static void quad(VertexConsumer consumer, Matrix4f matrix, TextureAtlasSprite sprite, Vec3 from, Vec3 to,
	    Vec3 offset, int r, int g, int b, int alpha) {
	float u0 = sprite.getU0();
	float u1 = sprite.getU1();
	float v0 = sprite.getV0();
	float v1 = sprite.getV1();
	vertex(consumer, matrix, from.subtract(offset), u0, v0, r, g, b, alpha);
	vertex(consumer, matrix, from.add(offset), u1, v0, r, g, b, alpha);
	vertex(consumer, matrix, to.add(offset), u1, v1, r, g, b, alpha);
	vertex(consumer, matrix, to.subtract(offset), u0, v1, r, g, b, alpha);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Vec3 pos, float u, float v, int r, int g,
	    int b, int alpha) {
	consumer.addVertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).setColor(r, g, b, alpha).setUv(u, v)
		.setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0, 1, 0);
    }
}