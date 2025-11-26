package io.github.mortuusars.exposure.client.image.modifier.pixel;

import net.minecraft.util.ARGB;

public class NegativeEffect implements PixelEffect {
    @Override
    public String getIdentifier() {
        return "negative";
    }

    public int modify(int color) {
        int alpha = ARGB.alpha(color);
        int red = ARGB.red(color);
        int green = ARGB.green(color);
        int blue = ARGB.blue(color);

        // Invert
        red = 255 - red;
        green = 255 - green;
        blue = 255 - blue;

        return ARGB.color(alpha, red, green, blue);
    }
}
