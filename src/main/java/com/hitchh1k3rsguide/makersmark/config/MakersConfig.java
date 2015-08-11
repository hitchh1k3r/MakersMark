package com.hitchh1k3rsguide.makersmark.config;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.CoreConfig;
import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.CoreUtils;
import com.hitchh1k3rsguide.makersmark.items.ItemInspectorsGlass;

public class MakersConfig
{

    public static boolean limitedInspection = false;
    public static boolean creativeCoins     = false;
    public static boolean creativeMailboxes = false;

    public static void init()
    {
        CoreUtils.addConfigOption(CoreUtils.getModHandler(), MakersMark.MODID, "limitedInspection", MakersMark.MODID + ".configgui.limitedInspection.tooltip", CoreConfig.ConfigType.BOOLEAN, false, CoreConfig.CONFIG_SYNCHED);
        CoreUtils.addConfigOption(CoreUtils.getModHandler(), MakersMark.MODID, "creativeCoins", MakersMark.MODID + ".configgui.creativeCoins.tooltip", CoreConfig.ConfigType.BOOLEAN, false, CoreConfig.CONFIG_CLIENT);
        CoreUtils.addConfigOption(CoreUtils.getModHandler(), MakersMark.MODID, "creativeMailboxes", MakersMark.MODID + ".configgui.creativeMailboxes.tooltip", CoreConfig.ConfigType.BOOLEAN, false, CoreConfig.CONFIG_CLIENT);
    }

    public static void setConfig(String name, Object value)
    {
        if ("limitedInspection".equals(name))
        {
            limitedInspection = (Boolean) value;
            setServerConfig(limitedInspection);
        }
        else if ("creativeCoins".equals(name))
        {
            creativeCoins = (Boolean) value;
        }
        else if ("creativeMailboxes".equals(name))
        {
            creativeMailboxes = (Boolean) value;
        }
    }

    public static void setServerConfig(boolean limitedMode)
    {
        ServerConfig.limitedInspection = limitedMode;
        ((ItemInspectorsGlass) MakersMark.getItems().inspectorsGlass).updateMode();
        ((ItemInspectorsGlass) MakersMark.getItems().advancedGlass).updateMode();
    }

    public static class ServerConfig
    {

        public static boolean limitedInspection;

    }

}
