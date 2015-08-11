package com.hitchh1k3rsguide.makersmark.util;

import com.hitchh1k3rsguide.makersmark.potions.MakersPotion;
import net.minecraft.potion.Potion;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PotionUtils
{

    private static boolean injected = false;

    private static void inject()
    {
        injected = true;
        Potion[] potionTypes;

        for (Field f : Potion.class.getDeclaredFields())
        {
            f.setAccessible(true);
            try
            {
                if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a"))
                {
                    Field modfield = Field.class.getDeclaredField("modifiers");
                    modfield.setAccessible(true);
                    modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                    potionTypes = (Potion[]) f.get(null);
                    if (potionTypes.length < 256)
                    {
                        final Potion[] newPotionTypes = new Potion[256];
                        System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
                        f.set(null, newPotionTypes);
                    }
                }
            }
            catch (Exception e)
            {
                Utils.raiseException("Unable to add custom potions.", e);
            }
        }
    }

    public static MakersPotion newPotion(Class<? extends MakersPotion> potionClass)
    {
        if (!injected)
        {
            inject();
        }

        try
        {
            for (int i = 32; i < Potion.potionTypes.length; ++i)
            {
                if (Potion.potionTypes[i] == null)
                {
                    return potionClass.getDeclaredConstructor(Integer.TYPE).newInstance(i);
                }
            }
        }
        catch (Exception e)
        {
            Utils.debugErr(e.getMessage());
        }
        Utils.raiseException("Unable to add custom potion (too many potions).", new Exception("Failed to add potion"));
        return null;
    }

}
