package com.hitchh1k3rsguide.makersmark;

import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.CoreConfig;
import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.CoreUtils;
import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.IModHandler;
import com.hitchh1k3rsguide.makersmark.config.MakersConfig;
import com.hitchh1k3rsguide.makersmark.events.ClientEvents;
import net.minecraft.item.Item;

public class MakersMarkHandler implements IModHandler
{

    @Override
    public void getMessage(Object sender, String key, Object value)
    {
        if (key.equals(CoreUtils.MESSAGE_CORE_HANDLER))
        {
            CoreUtils.setHandlers(value, this);
        }
        else if (key.equals(CoreUtils.MESSAGE_ADD_SHARED_ITEM))
        {
            String name = (String) (((Object[]) value)[0]);
            if ("goldenFeather".equals(name))
            {
                MakersMark.getItems().goldenFeather = (Item) (((Object[]) value)[1]);
            }
        }
        else if (key.equals(CoreUtils.MESSAGE_CORE_CONFIG))
        {
            CoreConfig.setCoreConfig((String) (((Object[]) value)[0]), (((Object[]) value)[1]));
        }
        else if (key.equals(CoreUtils.MESSAGE_SET_CONFIG))
        {
            MakersConfig.setConfig((String) (((Object[]) value)[0]), (((Object[]) value)[1]));
        }
        else if (key.equals(CoreUtils.MESSAGE_FIRST_RENDERABLE))
        {
            ClientEvents.firstRenderable();
        }
    }

    @Override
    public int hashCode()
    {
        return MakersMark.MODID.hashCode();
    }

}
