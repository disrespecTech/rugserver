package github.totorewa.rugserver.util.message;

import com.google.common.collect.Lists;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Iterator;
import java.util.List;

public class Message {
    public static final int RESET = 1;
    public static final int BLACK = 2;
    public static final int DARK_BLUE = 4;
    public static final int DARK_GREEN = 8;
    public static final int DARK_AQUA = 16;
    public static final int DARK_RED = 32;
    public static final int DARK_PURPLE = 64;
    public static final int GOLD = 128;
    public static final int GRAY = 256;
    public static final int DARK_GRAY = 512;
    public static final int BLUE = 1024;
    public static final int GREEN = 2048;
    public static final int AQUA = 4096;
    public static final int RED = 8192;
    public static final int LIGHT_PURPLE = 16384;
    public static final int YELLOW = 32768;
    public static final int WHITE = 65536;
    public static final int OBFUSCATED = 131072;
    public static final int BOLD = 262144;
    public static final int STRIKETHROUGH = 524288;
    public static final int UNDERLINE = 1048576;
    public static final int ITALIC = 2097152;

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

    public Text toText() {
        return createComponent(this);
    }

    public static Text createComponent(String message) {
        return new LiteralText(message);
    }

    public static Text createComponent(String message, int flags) {
        Text component = new LiteralText(message);
        applyStyle(component.getStyle(), flags);
        return component;
    }

    public static Text createComponent(Message message) {
        Text root = createComponent(message.text, message.flags);
        Text prev = root;
        Text curr;
        for (Message sib : message.siblings) {
            curr = createComponent(sib);
            prev.append(curr);
            prev = curr;
        }
        return root;
    }

    private static void applyStyle(Style style, int flags) {
        if ((flags & RESET) == RESET) {
            style.setFormatting(Formatting.RESET);
            style.setBold(false);
            style.setObfuscated(false);
            style.setStrikethrough(false);
            style.setUnderline(false);
            style.setItalic(false);
        }

        if ((flags & BLACK) == BLACK) style.setFormatting(Formatting.BLACK);
        else if ((flags & DARK_BLUE) == DARK_BLUE) style.setFormatting(Formatting.DARK_BLUE);
        else if ((flags & DARK_GREEN) == DARK_GREEN) style.setFormatting(Formatting.DARK_GREEN);
        else if ((flags & DARK_AQUA) == DARK_AQUA) style.setFormatting(Formatting.DARK_AQUA);
        else if ((flags & DARK_RED) == DARK_RED) style.setFormatting(Formatting.DARK_RED);
        else if ((flags & DARK_PURPLE) == DARK_PURPLE) style.setFormatting(Formatting.DARK_PURPLE);
        else if ((flags & GOLD) == GOLD) style.setFormatting(Formatting.GOLD);
        else if ((flags & GRAY) == GRAY) style.setFormatting(Formatting.GRAY);
        else if ((flags & DARK_GRAY) == DARK_GRAY) style.setFormatting(Formatting.DARK_GRAY);
        else if ((flags & BLUE) == BLUE) style.setFormatting(Formatting.BLUE);
        else if ((flags & GREEN) == GREEN) style.setFormatting(Formatting.GREEN);
        else if ((flags & AQUA) == AQUA) style.setFormatting(Formatting.AQUA);
        else if ((flags & RED) == RED) style.setFormatting(Formatting.RED);
        else if ((flags & LIGHT_PURPLE) == LIGHT_PURPLE) style.setFormatting(Formatting.LIGHT_PURPLE);
        else if ((flags & YELLOW) == YELLOW) style.setFormatting(Formatting.YELLOW);
        else if ((flags & WHITE) == WHITE) style.setFormatting(Formatting.WHITE);

        style.setBold((flags & BOLD) == BOLD);
        style.setObfuscated((flags & OBFUSCATED) == OBFUSCATED);
        style.setStrikethrough((flags & STRIKETHROUGH) == STRIKETHROUGH);
        style.setUnderline((flags & UNDERLINE) == UNDERLINE);
        style.setItalic((flags & ITALIC) == ITALIC);
    }
}
