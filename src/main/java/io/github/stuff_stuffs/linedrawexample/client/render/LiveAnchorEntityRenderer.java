package io.github.stuff_stuffs.linedrawexample.client.render;

import io.github.stuff_stuffs.linedrawexample.entity.LineAnchorEntity;
import io.github.stuff_stuffs.linedrawexample.mixin.WorldInvoker;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class LiveAnchorEntityRenderer extends EntityRenderer<LineAnchorEntity> {
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
        final int otherLight = dispatcher.getLight(otherEntity, tickDelta);
        //Use lerped pos to get smooth interpolation
        //VERY IMPORTANT otherwise the line is jerks around a lot
        final Vec3d lerpedPos = entity.getLerpedPos(tickDelta);
        final Vec3d delta = otherEntity.getLerpedPos(tickDelta).subtract(lerpedPos);
        final float x = (float) delta.x;
        final float y = (float) delta.y;
        final float z = (float) delta.z;
        Vec3d normal = delta.multiply(MathHelper.fastInverseSqrt(delta.lengthSquared()));
        float xNorm = (float) normal.x;
        float yNorm = (float) normal.y;
        float zNorm = (float) normal.z;
        VertexConsumer consumer = vertexConsumers.getBuffer(CustomRenderLayers.THICK_LINE);
        consumer.vertex(matrices.peek().getPositionMatrix(), 0, 0, 0).color(/*0xAARRGGBB*/0xFF_FF_FF_FF).normal(matrices.peek().getNormalMatrix(), xNorm, yNorm, zNorm).next();
        consumer.vertex(matrices.peek().getPositionMatrix(), x, y, z).color(/*0xAARRGGBB*/0xFF_FF_FF_FF).normal(matrices.peek().getNormalMatrix(), -xNorm, -yNorm, -zNorm).next();
    }

    @Override
    public boolean shouldRender(LineAnchorEntity entity, Frustum frustum, double x, double y, double z) {
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
