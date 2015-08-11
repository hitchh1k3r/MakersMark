package com.hitchh1k3rsguide.makersmark.network;

import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.Serializer;
import com.hitchh1k3rsguide.makersmark.containers.GUIMailbox;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageMailPlayerList implements IMessage
{

    private String[]  usernames;
    private boolean[] active;
    private boolean   restricted;
    private int       selected;

    public MessageMailPlayerList() {}

    public MessageMailPlayerList(String[] usernames, boolean[] active, int selected, boolean restricted)
    {
        this.usernames = usernames;
        this.active = active;
        this.restricted = restricted;
        this.selected = selected;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        Serializer.writeStringArray(buf, usernames);
        Serializer.writeBooleanArray(buf, active);
        Serializer.writeBooleanFlagByte(buf, restricted);
        buf.writeInt(selected);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        usernames = Serializer.readStringArray(buf);
        active = Serializer.readBooleanArray(buf);
        restricted = Serializer.readBooleanFlagByte(buf)[0];
        selected = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageMailPlayerList, IMessage>
    {

        @Override
        public IMessage onMessage(MessageMailPlayerList message, MessageContext ctx)
        {
            GUIMailbox.setUsernames(message.usernames, message.active, message.selected, message.restricted);
/*
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof GUIMailbox)
            {
                ((GUIMailbox) screen).setUsernames(message.usernames);
            }
*/
            return null;
        }

    }

}
