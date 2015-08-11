package com.hitchh1k3rsguide.makersmark.util;

import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.CoreConfig;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.containers.ContainerMailbox;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;


public class Utils
{

    public static File PLAYER_FOLDER;

    public static void reloadPlayerFolder()
    {
        try
        {
            PLAYER_FOLDER = new File(MinecraftServer.getServer().worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "playerdata");
        }
        catch (Exception ignored) {}
        ContainerMailbox.reloadMailCount();
    }

    public static void debugErr(String message)
    {
        if (CoreConfig.debugMode)
        {
            MakersMark.LOGGER.error(message);
        }
    }

    public static void debugMsg(String message)
    {
        if (CoreConfig.debugMode)
        {
            MakersMark.LOGGER.info(message);
        }
    }

    public static NBTTagCompound getPlayerPersistantData(EntityPlayer player)
    {
        NBTTagCompound nbt;

        if (!player.getEntityData().hasKey(EntityPlayer.PERSISTED_NBT_TAG))
        {
            nbt = new NBTTagCompound();
            player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, nbt);
        }
        else
        {
            nbt = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }

        return nbt;
    }

    public static String limitRenderString(FontRenderer renderer, String string, int width)
    {
        if (renderer.getStringWidth(string) > width)
        {
            int i = 0;
            int e = renderer.getCharWidth('.') * 3;
            for (char c : string.toCharArray())
            {
                width -= renderer.getCharWidth(c);
                if (width < e)
                {
                    string = string.substring(0, i) + "...";
                    break;
                }
                ++i;
            }
        }
        return string;
    }

    public static NBTTagCompound getPlayerTag(UUID uuid)
    {
        EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(uuid);
        if (player != null)
        {
            return player.getEntityData();
        }
        else
        {
            File file = new File(PLAYER_FOLDER, uuid.toString() + ".dat");
            try
            {
                if (file.exists() && file.isFile())
                {
                    NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
                    if (compound.hasKey("ForgeData"))
                    {
                        return compound.getCompoundTag("ForgeData");
                    }
                    else
                    {
                        return new NBTTagCompound();
                    }
                }
            }
            catch (IOException ignored) {}
        }
        return null;
    }

    public static void putPlayerTag(UUID uuid, NBTTagCompound compound)
    {
        EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(uuid);
        if (player != null)
        {
            NBTTagCompound write = player.getEntityData();
            for (Object key : compound.getKeySet())
            {
                write.setTag((String) key, compound.getTag((String) key));
            }
        }
        else
        {
            File file = new File(PLAYER_FOLDER, uuid.toString() + ".dat");
            try
            {
                if (file.exists() && file.isFile())
                {
                    NBTTagCompound data = CompressedStreamTools.readCompressed(new FileInputStream(file));
                    NBTTagCompound write;
                    if (data.hasKey("ForgeData"))
                    {
                        write = data.getCompoundTag("ForgeData");
                    }
                    else
                    {
                        write = new NBTTagCompound();
                        data.setTag("ForgeData", write);
                    }
                    for (Object key : compound.getKeySet())
                    {
                        write.setTag((String) key, compound.getTag((String) key));
                    }
                    CompressedStreamTools.writeCompressed(data, new FileOutputStream(file));
                }
            }
            catch (IOException ignored) {}
        }
    }

    public static Field getField(Class classRef, String deobfuscatedName, String obfuscatedName)
    {
        Field field = null;
        try
        {
            field = classRef.getDeclaredField(FMLForgePlugin.RUNTIME_DEOBF ? obfuscatedName : deobfuscatedName);
        }
        catch (Exception e)
        {
            MakersMark.LOGGER.error("Could not get field '" + deobfuscatedName + (FMLForgePlugin.RUNTIME_DEOBF ? "'" : "'*") + " ('" + obfuscatedName + (FMLForgePlugin.RUNTIME_DEOBF ? "'*" : "'") + ") from class '" + classRef.getName() + "'");
        }
        if (field != null)
        {
            field.setAccessible(true);
        }
        return field;
    }

    @SuppressWarnings("unchecked")
    public static Method getMethod(Class classRef, String deobfuscatedName, String obfuscatedName, Class<?>... params)
    {
        Method method = null;
        try
        {
            method = classRef.getDeclaredMethod(FMLForgePlugin.RUNTIME_DEOBF ? obfuscatedName : deobfuscatedName, params);
        }
        catch (Exception e)
        {
            MakersMark.LOGGER.error("Could not get method '" + deobfuscatedName + (FMLForgePlugin.RUNTIME_DEOBF ? "'" : "'*") + " ('" + obfuscatedName + (FMLForgePlugin.RUNTIME_DEOBF ? "'*" : "'") + ") from class '" + classRef.getName() + "'");
        }
        if (method != null)
        {
            method.setAccessible(true);
        }
        return method;
    }

    public static void raiseException(String msg, Exception e)
    {
        FMLCommonHandler.instance().raiseException(e, msg, true);
    }

    public static class WrapLine
    {

        public String line;
        public int    startIndex;

    }

    public static WrapLine[] wrapString(FontRenderer fontRenderer, String message, int width, int maxLines)
    {
        WrapLine[] ret = new WrapLine[maxLines];
        int spaceWidth = fontRenderer.getCharWidth(' ');
        int lineIndex = 0;
        int xOffset = 0;
        int characterIndex = 0;
        int wordWidth;
        ret[lineIndex] = new WrapLine();
        ret[lineIndex].startIndex = 0;
        ret[lineIndex].line = "";
        message += "\n.";
        String[] lines = message.split("\\n");
        for (int l = 0; l < lines.length - 1; ++l)
        {
            String line = lines[l];
            line += " .";
            String[] words = line.split(" ");
            for (int i = 0; i < words.length - 1; ++i)
            {
                String word = words[i];
                wordWidth = fontRenderer.getStringWidth(word) + spaceWidth;
                if (wordWidth > width)
                {
                    for (int c = 0; c < word.length(); ++c)
                    {
                        char ch = word.charAt(c);
                        int letterWidth = fontRenderer.getCharWidth(ch);
                        if (xOffset + letterWidth > width)
                        {
                            xOffset = 0;
                            ++lineIndex;
                            if (lineIndex >= maxLines)
                            {
                                return ret;
                            }
                            ret[lineIndex] = new WrapLine();
                            ret[lineIndex].startIndex = characterIndex;
                            ret[lineIndex].line = "";
                        }
                        characterIndex += 1;
                        xOffset += letterWidth;
                        ret[lineIndex].line += ch;
                    }
                    characterIndex += 1;
                    xOffset += spaceWidth;
                    ret[lineIndex].line += " ";
                }
                else
                {
                    if (xOffset + wordWidth > width)
                    {
                        ret[lineIndex].line = ret[lineIndex].line.substring(0, ret[lineIndex].line.length() - 1);
                        xOffset = 0;
                        ++lineIndex;
                        if (lineIndex >= maxLines)
                        {
                            return ret;
                        }
                        ret[lineIndex] = new WrapLine();
                        ret[lineIndex].startIndex = characterIndex;
                        ret[lineIndex].line = "";
                    }
                    characterIndex += word.length() + 1;
                    xOffset += wordWidth;
                    ret[lineIndex].line += word + " ";
                }
            }
            ret[lineIndex].line = ret[lineIndex].line.substring(0, ret[lineIndex].line.length() - 1);
            if (l < lines.length - 2)
            {
                xOffset = 0;
                ++lineIndex;
                if (lineIndex >= maxLines)
                {
                    return ret;
                }
                ret[lineIndex] = new WrapLine();
                ret[lineIndex].startIndex = characterIndex;
                ret[lineIndex].line = "";
            }
        }
        return Arrays.copyOfRange(ret, 0, lineIndex + 1);
    }

}
