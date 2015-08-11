package com.hitchh1k3rsguide.makersmark.network;

import com.hitchh1k3rsguide.makersmark.containers.IStringReciever;
import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.Serializer;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGUIString implements IMessage
{

    private String payload;

    public MessageGUIString()
    {
    }

    public MessageGUIString(String message)
    {
        this.payload = message;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        Serializer.writeString(buf, payload);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        payload = Serializer.readString(buf);
    }

    public static class Handler implements IMessageHandler<MessageGUIString, IMessage>
    {

        @Override
        public IMessage onMessage(MessageGUIString message, MessageContext ctx)
        {
            Container container = ctx.getServerHandler().playerEntity.openContainer;
            if (container instanceof IStringReciever)
            {
                ((IStringReciever) container).onString(message.payload);
            }
            return null;
        }

    }

}
