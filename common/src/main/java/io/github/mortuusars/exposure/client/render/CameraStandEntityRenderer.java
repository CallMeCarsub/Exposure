package io.github.mortuusars.exposure.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.PlatformHelperClient;
import io.github.mortuusars.exposure.client.render.state.CameraStandEntityRenderState;
import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.world.entity.CameraStandEntity;
import io.github.mortuusars.exposure.world.item.camera.CameraItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CameraStandEntityRenderer <T extends CameraStandEntity> extends EntityRenderer<T, CameraStandEntityRenderState> {
    public static final float MOUNT_SCALE = 0.9f;

    protected final BlockRenderDispatcher blockRenderer;

    public CameraStandEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public @NotNull CameraStandEntityRenderState createRenderState() {
        return new CameraStandEntityRenderState();
    }

    @Override
    public void extractRenderState(T entity, CameraStandEntityRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.hurtDir = entity.getHurtDir();
        reusedState.hurtTime = (float)entity.getHurtTime() - partialTick;
        reusedState.damageTime = Math.max(0, entity.getDamage() - partialTick);
        reusedState.entityPitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        reusedState.entityYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        reusedState.inVehicle = entity.getVehicle() != null;
        reusedState.camera = entity.getCamera();
        if (reusedState.inVehicle) {
            reusedState.vehicleRot = Mth.lerp(partialTick, entity.getVehicle().yRotO, entity.getVehicle().getYRot());
        }
    }

    @Override
    public void render(CameraStandEntityRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(state, poseStack, bufferSource, packedLight);

        float hurtTime = state.hurtTime;
        float damage = state.damageTime;
        if (hurtTime > 0.0F) {
            float rotation = Mth.sin(hurtTime) * hurtTime * damage / 10.0F * (float) state.hurtDir;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation));
        }

        float entityPitch = state.entityPitch;

        renderStand(state, poseStack, bufferSource, packedLight);
        renderMount(state, poseStack, bufferSource, packedLight);
        if (!state.camera.isEmpty()) {
            renderCamera(state, poseStack, bufferSource, packedLight);
        }
    }

    private void renderStand(CameraStandEntityRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        if (state.inVehicle) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-state.vehicleRot + 45));
        }

        poseStack.translate(-0.5f, 0f, -0.5f);

        ModelResourceLocation modelLocation = ExposureClient.Models.CAMERA_STAND;
        BakedModel model = PlatformHelperClient.getModel(modelLocation);
        blockRenderer.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()),
                null, model, 1.0f, 1.0f, 1.0f, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private void renderMount(CameraStandEntityRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 1.125, 0);
        float scale = MOUNT_SCALE;
        poseStack.scale(scale, scale, scale);

        float entityYaw = state.entityYaw;
        float entityPitch = state.entityPitch;

        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw + 180));
        poseStack.mulPose(Axis.XP.rotationDegrees(-entityPitch));

        if (state.isMalfunctioned) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-50));
            poseStack.mulPose(Axis.XP.rotationDegrees(-10));
        }

        poseStack.translate(-0.5f, 0f, -0.5f);
        ModelResourceLocation mountModelLocation = ExposureClient.Models.CAMERA_STAND_MOUNT;
        BakedModel mountModel = PlatformHelperClient.getModel(mountModelLocation);
        blockRenderer.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()),
                null, mountModel, 1.0f, 1.0f, 1.0f, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private void renderCamera(CameraStandEntityRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float entityYaw = state.entityYaw;
        float entityPitch = state.entityPitch;

        poseStack.translate(0, 1.125, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw + 180));
        poseStack.mulPose(Axis.XP.rotationDegrees(-entityPitch));
        poseStack.translate(0, 0.125 * MOUNT_SCALE, 0);

        if (state.isMalfunctioned) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-50));
            poseStack.mulPose(Axis.XP.rotationDegrees(-15));
        }

        ItemStack camera = state.camera;
        float scale = camera.getItem() instanceof CameraItem cameraItem ? cameraItem.getScaleOnStand() : MOUNT_SCALE;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, 0.5, 0);

        Minecrft.get().getItemRenderer().renderStatic(camera, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, null, 0);
        poseStack.popPose();
    }
}
