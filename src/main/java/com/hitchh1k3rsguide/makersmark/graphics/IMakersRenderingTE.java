package com.hitchh1k3rsguide.makersmark.graphics;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public interface IMakersRenderingTE
{

    void renderTileEntityAt(TileEntity tileEntity, double renderX, double renderY, double renderZ, float partialTick, int blockDamage, TileEntitySpecialRenderer renderer);

}
