package com.hitchh1k3rsguide.makersmark.asm;

import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.HitchCore;
import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.VersionInfo;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import java.util.List;
import java.util.Map;

// -Dfml.coreMods.load=com.hitchh1k3rsguide.makersmark.asm.MakersMarkCorePlugin

@TransformerExclusions(value = { "com.hitchh1k3rsguide.makersmark.asm" })
@SortingIndex(1001)
public class CorePlugin implements IFMLLoadingPlugin
{

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{ ASMTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass()
    {
        String hitchCore = "com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.HitchCore";
        VersionInfo version = new VersionInfo(HitchCore.VERSION);

        List<String> containers = FMLInjectionData.containers;

        for (int i = 0; i < containers.size(); ++i)
        {
            String containerClass = containers.get(i);
            if (containerClass.startsWith("com.hitchh1k3rsguide.") && containerClass.endsWith(".HitchCore"))
            {
                try
                {
                    VersionInfo cV = new VersionInfo((String) Class.forName(containerClass).getDeclaredField("VERSION").get(null));
                    if (version.compareTo(cV) > 0)
                    {
                        containers.set(i, hitchCore);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }
        return hitchCore;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }

}
