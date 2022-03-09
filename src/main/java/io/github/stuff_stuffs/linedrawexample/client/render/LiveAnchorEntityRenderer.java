package io.github.stuff_stuffs.linedrawexample.client.render;

import io.github.stuff_stuffs.linedrawexample.entity.LineAnchorEntity;
import io.github.stuff_stuffs.linedrawexample.mixin.WorldInvoker;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.util.UUID;

public class LiveAnchorEntityRenderer extends EntityRenderer<LineAnchorEntity> {
    private static final float RADIUS = 0.25f;
    private static final Quaternion X_90_ROT = Vec3f.POSITIVE_Y.getDegreesQuaternion(90);
    private static final Vec3d UP = new Vec3d(0, 1, 0);

    public LiveAnchorEntityRenderer(final EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(final LineAnchorEntity entity, final float yaw, final float tickDelta, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        final UUID uuid = entity.getHolderUuid();
        if (uuid == null) {
            return;
        }
        final Entity otherEntity = ((WorldInvoker) entity.world).getEntityLookup().get(uuid);
        if (otherEntity == null) {
            return;
        }
        final Vec3d start = entity.getLerpedPos(tickDelta);
        final Vec3d end = otherEntity.getLerpedPos(tickDelta);
        Vec3d delta = end.subtract(start);
        delta = delta.multiply(MathHelper.fastInverseSqrt(delta.lengthSquared()));
        matrices.push();
        if (!UP.equals(delta)) {
            final Vec3d axis = UP.crossProduct(delta).normalize();
            double theta = Math.acos(UP.dotProduct(delta));
            final Vec3d b = axis.crossProduct(UP);
            if (b.dotProduct(delta) < 0) {
                theta = -theta;
            }
            float c = MathHelper.cos((float) (theta/2.0));
            float s = MathHelper.sin((float) (theta/2.0));
            final Quaternion quaternion = new Quaternion( s * (float)axis.x, s * (float)axis.y, s * (float)axis.z, c);
            matrices.multiply(quaternion);
        }
        final int startLight = light;
        final int endLight = dispatcher.getLight(otherEntity, tickDelta);

        render(matrices, vertexConsumers, start, end, startLight, endLight);
        matrices.pop();
    }

    private static void render(final MatrixStack matrixStack, final VertexConsumerProvider consumers, final Vec3d start, final Vec3d end, final int startLight, final int endLight) {
        final VertexConsumer consumer = consumers.getBuffer(RenderLayer.getEntitySolid(/*Your Texture here*/BeaconBlockEntityRenderer.BEAM_TEXTURE));
        final double length = end.distanceTo(start);
        renderCap(consumer, 0, startLight, false, matrixStack);
        renderCap(consumer, length, endLight, true, matrixStack);
        side(matrixStack, consumer, length, startLight, endLight);
        matrixStack.multiply(X_90_ROT);
        side(matrixStack, consumer, length, startLight, endLight);
        matrixStack.multiply(X_90_ROT);
        side(matrixStack, consumer, length, startLight, endLight);
        matrixStack.multiply(X_90_ROT);
        side(matrixStack, consumer, length, startLight, endLight);
    }

    private static void renderCap(VertexConsumer vertexConsumer, double d, int light, boolean end, MatrixStack stack) {
        final Matrix4f model = stack.peek().getPositionMatrix();
        final Matrix3f normal = stack.peek().getNormalMatrix();
        if(end) {
            vertexConsumer.vertex(model, RADIUS, (float)d, -RADIUS).color(255,255,255,255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
            vertexConsumer.vertex(model, -RADIUS, (float)d, -RADIUS).color(255,255,255,255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
            vertexConsumer.vertex(model, -RADIUS, (float)d, RADIUS).color(255,255,255,255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
            vertexConsumer.vertex(model, RADIUS, (float)d, RADIUS).color(255,255,255,255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        } else {
            vertexConsumer.vertex(model, RADIUS, (float)d, RADIUS).color(255,255,255,255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
            vertexConsumer.vertex(model, -RADIUS, (float)d, RADIUS).color(255,255,255,255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
            vertexConsumer.vertex(model, -RADIUS, (float)d, -RADIUS).color(255,255,255,255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
            vertexConsumer.vertex(model, RADIUS, (float)d, -RADIUS).color(255,255,255,255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        }
    }

    private static void side(final MatrixStack stack, final VertexConsumer vertexConsumer, final double length, final int lightStart, final int lightEnd) {
        final Matrix4f model = stack.peek().getPositionMatrix();
        final Matrix3f normal = stack.peek().getNormalMatrix();
        vertexConsumer.vertex(model, RADIUS, 0, -RADIUS).color(255,255,255,255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(lightStart).normal(normal, 1, 0, 0).next();
        vertexConsumer.vertex(model, RADIUS, (float) length, -RADIUS).color(255,255,255,255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(lightEnd).normal(normal, 1, 0, 0).next();
        vertexConsumer.vertex(model, RADIUS, (float) length, RADIUS).color(255,255,255,255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(lightEnd).normal(normal, 1, 0, 0).next();
        vertexConsumer.vertex(model, RADIUS, 0, RADIUS).color(255,255,255,255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(lightStart).normal(normal, 1, 0, 0).next();
    }

    @Override
    public boolean shouldRender(final LineAnchorEntity entity, final Frustum frustum, final double x, final double y, final double z) {
        final UUID uuid = entity.getHolderUuid();
        if (uuid == null) {
            return super.shouldRender(entity, frustum, x, y, z);
        }
        final Entity otherEntity = ((WorldInvoker) entity.world).getEntityLookup().get(uuid);
        if (otherEntity == null) {
            return super.shouldRender(entity, frustum, x, y, z);
        }
        return super.shouldRender(entity, frustum, x, y, z) || dispatcher.shouldRender(otherEntity, frustum, otherEntity.getX(), otherEntity.getY(), otherEntity.getZ());
    }

    @Override
    public Identifier getTexture(final LineAnchorEntity entity) {
        throw new UnsupportedOperationException();
    }
}
