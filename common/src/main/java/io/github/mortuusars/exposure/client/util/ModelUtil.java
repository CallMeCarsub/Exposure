package io.github.mortuusars.exposure.client.util;

import net.minecraft.client.model.geom.ModelPart;

public class ModelUtil {
    public static void copyFrom(ModelPart toAffect, ModelPart modelPart) {
        toAffect.xScale = modelPart.xScale;
        toAffect.yScale = modelPart.yScale;
        toAffect.zScale = modelPart.zScale;
        toAffect.xRot = modelPart.xRot;
        toAffect.yRot = modelPart.yRot;
        toAffect.zRot = modelPart.zRot;
        toAffect.x = modelPart.x;
        toAffect.y = modelPart.y;
        toAffect.z = modelPart.z;
    }
}
