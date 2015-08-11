package com.hitchh1k3rsguide.makersmark.network;

import com.hitchh1k3rsguide.makersmark.containers.IButtonReciver;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGUIButton implements IMessage
{

    private int id;

    public MessageGUIButton()
    {
    }

    public MessageGUIButton(int id)
    {
        this.id = id;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageGUIButton, IMessage>
    {

        @Override
        public IMessage onMessage(MessageGUIButton message, MessageContext ctx)
        {
            Container container = ctx.getServerHandler().playerEntity.openContainer;
            if (container instanceof IButtonReciver)
            {
                ((IButtonReciver) container).onButton(message.id);
            }
            return null;
        }

    }

}
