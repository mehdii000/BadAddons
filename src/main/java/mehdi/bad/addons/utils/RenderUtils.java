package mehdi.bad.addons.utils;

import mehdi.bad.addons.BadAddons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtils {

    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final WorldRenderer worldRenderer;
    private static Integer drawMode;
    private static boolean retainTransforms;

    private static Long colorized;

    static {
        worldRenderer = tessellator.getWorldRenderer();
        colorized = null;
        drawMode = null;
        retainTransforms = false;
    }

    public static void retainTransforms(boolean var0) {
        retainTransforms = var0;
        finishDraw();
    }

    public static int getScreenHeight() {
        return (new ScaledResolution(BadAddons.mc)).getScaledHeight();
    }

    public static void drawStringWithShadow(String var0, float var1, float var2) {
    	BadAddons.mc.fontRendererObj.drawString(ChatLib.addColor(var0), var1, var2, -1, true);
    }

    public static void renderStringWithItems(String text, double x, double y, int color, boolean shadow) {
        double cursorX = x;
        String[] parts = text.split(" ");

        for (String part : parts) {
            if (part.startsWith(":") && part.endsWith(":")) {
                // Extract item name from placeholder
                String itemName = part.substring(1, part.length() - 1).toLowerCase();
                ItemStack itemStack = getItemStackByName(itemName);

                if (itemStack != null) {
                    renderItemStack(itemStack, cursorX, y-1);
                    cursorX += 18; // Space between items
                }
            } else {
                // Render the string part
                BadAddons.mc.fontRendererObj.drawString(part, (float) cursorX, (float) y, color, shadow);
                cursorX += BadAddons.mc.fontRendererObj.getStringWidth(part) + 4; // Space between words
            }
        }
    }

    private static void renderItemStack(ItemStack stack, double x, double y) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y - 4, 0);
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, 0, 0);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    private static ItemStack getItemStackByName(String name) {
        ResourceLocation resourceLocation = new ResourceLocation(name);
        Item item = Item.itemRegistry.getObject(resourceLocation);

        if (item != null) {
            return new ItemStack(item);
        }

        return null;
    }

    public static void translate(float var0, float var1, float var2) {
        GlStateManager.translate(var0, var1, var2);
    }

    public static void drawString(String var0, float var1, int var2, int var3, Color var4, boolean var5) {
        GlStateManager.enableBlend();
        GlStateManager.scale(var1, var1, var1);
        GuiUtils.drawString(var0, MathUtils.ceil((float) var2 / var1), MathUtils.ceil((float) var3 / var1), var5, var4);
        GlStateManager.disableBlend();
    }

    public static void drawRect(int var0, int var1, int var2, int var3, int var4) {
        Gui.drawRect(var1, var2, var1 + var3, var2 + var4, var0);
    }

    public static void translate(double var0, double var2) {
        translate((float) var0, (float) var2);
    }

    public static int getStringWidth(String var0) {
        return BadAddons.mc.fontRendererObj.getStringWidth(var0);
    }

    public static int getStringHeight(String var0) {
        return BadAddons.mc.fontRendererObj.FONT_HEIGHT;
    }

    public static void start() {
        GlStateManager.pushMatrix();
    }

    public static void translate(float var0, float var1) {
        GlStateManager.translate(var0, var1, 0.0F);
    }

    private static void doColor(long var0) {
        int var2 = (int) var0;
        if (colorized == null) {
            float var3 = (float) (var2 >> 24 & 255) / 255.0F;
            float var4 = (float) (var2 >> 16 & 255) / 255.0F;
            float var5 = (float) (var2 >> 8 & 255) / 255.0F;
            float var6 = (float) (var2 & 255) / 255.0F;
            GlStateManager.color(var4, var5, var6, var3);
        }

    }

    public static void drawScaledCenteredString(String text, int x, int y, float scale) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        int textWidth = BadAddons.mc.fontRendererObj.getStringWidth(text);
        int centerX = Math.round(x / scale - textWidth / 2);
        int centerY = Math.round(y / scale - BadAddons.mc.fontRendererObj.FONT_HEIGHT / 2);
        BadAddons.mc.fontRendererObj.drawStringWithShadow(text, centerX, centerY, 1);
        GL11.glPopMatrix();
    }

    public static void finishDraw() {
        if (!retainTransforms) {
            colorized = null;
            drawMode = null;
            GL11.glPopMatrix();
            GL11.glPushMatrix();
        }

    }

    public static int getStringWidthWithItem(String s) {
        String modifiedString = s.replaceAll(":[^:]*:", "_");
        return BadAddons.mc.fontRendererObj.getStringWidth(modifiedString);
    }

}
