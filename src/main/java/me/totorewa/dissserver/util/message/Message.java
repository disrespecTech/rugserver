package me.totorewa.dissserver.util.message;

import com.google.common.collect.Lists;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Iterator;
import java.util.List;

public class Message {
    public static final int RESET = 0;
    public static final int BLACK = 1;
    public static final int DARK_BLUE = 2;
    public static final int DARK_GREEN = 4;
    public static final int DARK_AQUA = 8;
    public static final int DARK_RED = 16;
    public static final int DARK_PURPLE = 32;
    public static final int GOLD = 64;
    public static final int GRAY = 128;
    public static final int DARK_GRAY = 256;
    public static final int BLUE = 512;
    public static final int GREEN = 1024;
    public static final int AQUA = 2048;
    public static final int RED = 4096;
    public static final int LIGHT_PURPLE = 8192;
    public static final int YELLOW = 16384;
    public static final int WHITE = 32768;
    public static final int OBFUSCATED = 65536;
    public static final int BOLD = 131072;
    public static final int STRIKETHROUGH = 262144;
    public static final int UNDERLINE = 524288;
    public static final int ITALIC = 1048576;

    private final String text;
    private final int flags;
    private final List<Message> siblings = Lists.<Message>newArrayList();

    public Message() {
        this("", 0);
    }

    public Message(String text) {
        this(text, 0);
    }

    public Message(String text, int flags) {
        this.text = text;
        this.flags = flags;
    }

    public Iterable<Message> getSiblings() {
        return new Iterable<Message>() {
            @Override
            public Iterator<Message> iterator() {
                return siblings.iterator();
            }
        };
    }

    public Message add(String text) {
        return add(new Message(text));
    }

    public Message add(String text, int flags) {
        return add(new Message(text, flags));
    }

    public Message add(Message message) {
        this.siblings.add(message);
        return this;
    }

    public static IChatComponent createComponent(String message) {
        return new ChatComponentText(message);
    }

    public static IChatComponent createComponent(String message, int flags) {
        IChatComponent component = new ChatComponentText(message);
        applyStyle(component.getChatStyle(), flags);
        return component;
    }

    public static IChatComponent createComponent(Message message) {
        IChatComponent root = createComponent(message.text, message.flags);
        IChatComponent prev = root;
        IChatComponent curr;
        for (Message sib : message.siblings) {
            curr = createComponent(sib);
            prev.appendSibling(curr);
            prev = curr;
        }
        return root;
    }

    private static void applyStyle(ChatStyle style, int flags) {
        if (flags == RESET) {
            style.setColor(EnumChatFormatting.RESET);
            style.setBold(false);
            style.setObfuscated(false);
            style.setStrikethrough(false);
            style.setUnderlined(false);
            style.setItalic(false);
            return;
        }

        if ((flags & BLACK) == BLACK) style.setColor(EnumChatFormatting.BLACK);
        else if ((flags & DARK_BLUE) == DARK_BLUE) style.setColor(EnumChatFormatting.DARK_BLUE);
        else if ((flags & DARK_GREEN) == DARK_GREEN) style.setColor(EnumChatFormatting.DARK_GREEN);
        else if ((flags & DARK_AQUA) == DARK_AQUA) style.setColor(EnumChatFormatting.DARK_AQUA);
        else if ((flags & DARK_RED) == DARK_RED) style.setColor(EnumChatFormatting.DARK_RED);
        else if ((flags & DARK_PURPLE) == DARK_PURPLE) style.setColor(EnumChatFormatting.DARK_PURPLE);
        else if ((flags & GOLD) == GOLD) style.setColor(EnumChatFormatting.GOLD);
        else if ((flags & GRAY) == GRAY) style.setColor(EnumChatFormatting.GRAY);
        else if ((flags & DARK_GRAY) == DARK_GRAY) style.setColor(EnumChatFormatting.DARK_GRAY);
        else if ((flags & BLUE) == BLUE) style.setColor(EnumChatFormatting.BLUE);
        else if ((flags & GREEN) == GREEN) style.setColor(EnumChatFormatting.GREEN);
        else if ((flags & AQUA) == AQUA) style.setColor(EnumChatFormatting.AQUA);
        else if ((flags & RED) == RED) style.setColor(EnumChatFormatting.RED);
        else if ((flags & LIGHT_PURPLE) == LIGHT_PURPLE) style.setColor(EnumChatFormatting.LIGHT_PURPLE);
        else if ((flags & YELLOW) == YELLOW) style.setColor(EnumChatFormatting.YELLOW);
        else if ((flags & WHITE) == WHITE) style.setColor(EnumChatFormatting.WHITE);

        style.setBold((flags & BOLD) == BOLD);
        style.setObfuscated((flags & OBFUSCATED) == OBFUSCATED);
        style.setStrikethrough((flags & STRIKETHROUGH) == STRIKETHROUGH);
        style.setUnderlined((flags & UNDERLINE) == UNDERLINE);
        style.setItalic((flags & ITALIC) == ITALIC);
    }
}
