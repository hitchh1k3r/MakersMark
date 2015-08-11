package com.hitchh1k3rsguide.makersmark.graphics;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class TileEntityRenderer extends TileEntitySpecialRenderer
{

    private static TileEntityRenderer instance = new TileEntityRenderer();

    public static void register(Class<? extends TileEntity> teClass)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(teClass, instance);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double renderX, double renderY, double renderZ, float partialTick, int blockDamage)
    {
        if (tileEntity instanceof IMakersRenderingTE)
        {
            ((IMakersRenderingTE) tileEntity).renderTileEntityAt(tileEntity, renderX, renderY, renderZ, partialTick, blockDamage, this);
        }
    }

}
