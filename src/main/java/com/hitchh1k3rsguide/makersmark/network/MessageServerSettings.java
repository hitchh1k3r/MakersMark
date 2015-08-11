package com.hitchh1k3rsguide.makersmark.network;

import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.Serializer;
import com.hitchh1k3rsguide.makersmark.config.MakersConfig;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSettings implements IMessage
{

    private boolean limitedMode;

    public MessageServerSettings()
    {
        limitedMode = MakersConfig.limitedInspection;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        Serializer.writeBooleanFlagByte(buf, limitedMode);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        boolean[] flags = Serializer.readBooleanFlagByte(buf);
        limitedMode = flags[0];
    }

    public void processPacket()
    {
        MakersConfig.setServerConfig(limitedMode);
    }

    public static class Handler implements IMessageHandler<MessageServerSettings, IMessage>
    {

        @Override
        public IMessage onMessage(MessageServerSettings message, MessageContext ctx)
        {
            message.processPacket();
            return null;
        }

    }

}