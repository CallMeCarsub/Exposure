package io.github.mortuusars.exposure;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelIdentifier;
import net.minecraft.resources.Identifier;

public class PlatformHelperClient {
    @ExpectPlatform
    public static BakedModel getModel(ModelIdentifier model) {
        throw new AssertionError();
    }
}
