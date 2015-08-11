package com.hitchh1k3rsguide.makersmark.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.nio.ByteBuffer;

public class GraphicUtils
{

    @SideOnly(Side.CLIENT)
    public static Color[] getIconColors(TextureAtlasSprite[] sprites)
    {
        if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().getTextureManager() != null)
        {
            Color[] colors = new Color[sprites.length];
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            int imageWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0,
                                                          GL11.GL_TEXTURE_WIDTH);
            int imageHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0,
                                                           GL11.GL_TEXTURE_HEIGHT);
            ByteBuffer image = ByteBuffer.allocateDirect(imageWidth * imageHeight * 4);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA,
                               GL12.GL_UNSIGNED_INT_8_8_8_8_REV, image);
            for (int i = 0; i < sprites.length; ++i)
            {
                TextureAtlasSprite sprite = sprites[i];
                int left = MathHelper.floor_float(sprite.getMinU() * imageWidth);
                int right = MathHelper.floor_float(sprite.getMaxU() * imageWidth);
                int top = MathHelper.floor_float(sprite.getMinV() * imageHeight);
                int bottom = MathHelper.floor_float(sprite.getMaxV() * imageHeight);

                float totalBlue = 0;
                float totalGreen = 0;
                float totalRed = 0;
                float totalWeight = 0;
                for (int y = top; y <= bottom; ++y)
                {
                    for (int x = left; x <= right; ++x)
                    {
                        int pixelIndex = x + (y * imageWidth);
                        float alpha = (image.get(pixelIndex * 4 + 3) & 0xFF) / 255;
                        totalBlue += (image.get(pixelIndex * 4 + 0) & 0xFF) * alpha / 255;
                        totalGreen += (image.get(pixelIndex * 4 + 1) & 0xFF) * alpha / 255;
                        totalRed += (image.get(pixelIndex * 4 + 2) & 0xFF) * alpha / 255;
                        totalWeight += alpha;
                    }
                }
                float red = totalRed / totalWeight;
                float green = totalGreen / totalWeight;
                float blue = totalBlue / totalWeight;
                colors[i] = new Color(red, green, blue);
            }
            return colors;
        }
        return null;
    }

}
