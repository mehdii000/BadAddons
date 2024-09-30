package mehdi.bad.addons.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatLib {

    public static boolean toggleOff = false;

    public static String removeFormatting(String var0) {
        return var0 == null ? null : var0.replaceAll("[\\u00a7&][0-9a-zA-Z]", "");
    }

    public static void playerJoin(String var0) {
        if (!toggleOff) {
            String var1 = "&b[§9BadAddons§b] &r" + var0 + " &ejoined.";
            var1 = addColor(var1);
            ChatComponentText var2 = new ChatComponentText(var1);
            addComponent(var2);
        }
    }

    public static void addComponent(IChatComponent var0, boolean var1) {
        if (MinecraftUtils.getPlayer() != null) {
            try {
                ClientChatReceivedEvent var2 = new ClientChatReceivedEvent((byte) 0, var0);
                if (var1 && MinecraftForge.EVENT_BUS.post(var2)) {
                    return;
                }

                MinecraftUtils.getPlayer().addChatMessage(var2.message);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }
    }

    public static void addComponent(IChatComponent var0) {
        addComponent(var0, true);
    }

    public static String addColor(String var0) {
        if (var0 == null) {
            return "";
        } else {
            Pattern var1 = Pattern.compile("((?<!\\\\))&(?![^0-9a-fklmnor]|$)");
            Matcher var2 = var1.matcher(var0);
            return var2.replaceAll("§");
        }
    }

    public static void chat(String var0, boolean var1) {
        if (var0 == null) {
            var0 = "null";
        }

        String[] var2 = var0.split("\n");
        int var4 = var2.length;

        for (String s : var2) {
            String var6 = s;
            var6 = "&r" + var6;
            var6 = addColor(var6);
            System.out.println(var6);
            ChatComponentText var7 = new ChatComponentText(var6);
            addComponent(var7, var1);
        }
    }

    public static void say(String var0) {
        if (var0 == null) {
            var0 = "null";
        }

        EntityPlayerSP var1 = MinecraftUtils.getPlayer();
        String[] var2 = var0.split("\n");
        int var4 = var2.length;

        for (String var6 : var2) {
            if (var1 != null) {
                var1.sendChatMessage(var6);
            }
        }

    }

    public static String removeColor(String var0) {
        if (var0 == null) {
            return "";
        } else {
            Pattern var1 = Pattern.compile("((?<!\\\\))§(?![^0-9a-fklmnor]|$)");
            Matcher var2 = var1.matcher(var0);
            return var2.replaceAll("&");
        }
    }

    public static void chat(String text) {
        chat(text, true);
    }

    public static void toggle() {
        toggleOff = !toggleOff;
        chat((toggleOff ? "&cDisabled" : "&aEnabled") + "&r&b BadAddons Chat!");
    }

    public static void showItem(int var0, String var1, String var2, String var3) {
        if (!toggleOff) {
            if (var0 == 3) {
                String var4 = "&b[§9BadAddons§b] &r" + var1 + "&r&f: &rShowed item ";
                var4 = addColor(var4);
                ChatComponentText var5 = new ChatComponentText(var4);
                ChatComponentText var6 = new ChatComponentText(var2);
                ChatStyle var7 = new ChatStyle();
                var7.setChatHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ChatComponentText(var3)));
                var6.setChatStyle(var7);
                var5.appendSibling(var6);
                addComponent(var5);
            }
        }
    }
    
    public static void sendHoverMessage(String message, String hoverCommand, String s) {
        Minecraft minecraft = Minecraft.getMinecraft();
        IChatComponent chatComponent = new ChatComponentText(message);
        chatComponent.setChatStyle(new ChatStyle()
            .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentText(s)))
            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, hoverCommand)));
        minecraft.thePlayer.addChatMessage(chatComponent);
    }

    public static void sendHoverMessageExtra(String message, String hoverCommand, String s, String ExtraHover, String extraHoverCmd) {
        Minecraft minecraft = Minecraft.getMinecraft();

        IChatComponent chatComponent = null;
        try {
            chatComponent = new ChatComponentText(new String(message.getBytes(StandardCharsets.UTF_8), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        IChatComponent extraComp = new ChatComponentText(" " + ExtraHover);
        try {
            chatComponent.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentText( new String(s.getBytes(StandardCharsets.UTF_8), "UTF-8") )))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, hoverCommand)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        extraComp.setChatStyle(new ChatStyle()
                .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentText("Click")))
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, extraHoverCmd)));
        minecraft.thePlayer.addChatMessage(chatComponent.appendSibling(extraComp));
    }

    public static String getPrefix(String var0) {
        if (var0 == null) {
            return "";
        } else {
            Pattern var1 = Pattern.compile("^(&[0-9a-fklmnor])*");
            Matcher var2 = var1.matcher(var0);
            return var2.find() ? var2.group(0) : "&r";
        }
    }
}
