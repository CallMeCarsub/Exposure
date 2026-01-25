package io.github.mortuusars.exposure.client.render.state;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CameraStandEntityRenderState extends EntityRenderState {
    public int hurtDir;
    public float hurtTime;
    public float damageTime;
    public float entityPitch;
    public float entityYaw;
    public float vehicleRot;
    public boolean isMalfunctioned;
    public boolean inVehicle;
    public ItemStack camera;
    @Nullable
    public EntityModel cameraModel;

    public CameraStandEntityRenderState() {
        this.camera = ItemStack.EMPTY;
    }
}
