package mehdi.bad.addons.utils;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Objects.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiUtils {

    private static final Tessellator tessellator = Tessellator.getInstance();

    private static final ResourceLocation beaconBeam;

    private static final WorldRenderer worldRenderer;

    static {
        worldRenderer = tessellator.getWorldRenderer();
        beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");
    }

    public static void drawTexture(ResourceLocation var0, int var1, int var2, int var3, int var4) {
        drawTexture(var0, var1, var2, var3, var4, var3, var4, 0, 0);
    }

    public static void drawRotatedTexture(ResourceLocation var0, int var1, int var2, int var3, int var4, int var5) {
        drawRotatedTexture(var0, var1, var2, var3, var4, var3, var4, 0, 0, var5);
    }

    public static void drawStringOnSlot(String var0, int var1, int var2, int var3, float var4, int var5, int var6) {
        Vector2i var7 = getXYForSlot(var1, var2, var3);
        int var8 = var7.x + var5;
        int var9 = var7.y + var6;
        GlStateManager.translate(0.0F, 0.0F, 1.0F);
        GlStateManager.pushMatrix();
        drawScaledString(var0, var4, var8, var9, false);
        GlStateManager.popMatrix();
        GlStateManager.translate(0.0F, 0.0F, -1.0F);
    }

    public static void drawRotatedTexture(ResourceLocation var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) var1 + (float) var3 / 2.0F, (float) var2 + (float) var4 / 2.0F, 0.0F);
        GlStateManager.rotate((float) var9, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate((float) (-var1) - (float) var3 / 2.0F, (float) (-var2) - (float) var4 / 2.0F, 0.0F);
        drawTexture(var0, var1, var2, var3, var4, var5, var6, var7, var8);
        GlStateManager.popMatrix();
    }

    public static void drawStringOnSlot(String var0, int var1, int var2, int var3, Color var4) {
        Vector2i var5 = getXYForSlot(var1, var2, var3);
        int var6 = var5.x + 8 - RenderUtils.getStringWidth(var0) / 2;
        int var7 = var5.y + 4;
        GL11.glTranslated(0.0, 0.0, 1.0);
        drawString(var0, var6, var7, false, var4);
        GL11.glTranslated(0.0, 0.0, -1.0);
    }

    private static void drawFilledBoundingBoxAbsolute(float var0, float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9) {
        EntityPlayerSP var10 = MinecraftUtils.getPlayer();
        float var11 = MathUtils.getX(var10);
        float var12 = MathUtils.getY(var10);
        float var13 = MathUtils.getZ(var10);
        var0 -= var11;
        var1 -= var12;
        var2 -= var13;
        var3 -= var11;
        var4 -= var12;
        var5 -= var13;
        drawFilledBoundingBoxRelative(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }

    public static void drawString(String var0, int var1, int var2, boolean var3) {
        drawString(var0, var1, var2, var3, Color.WHITE);
    }

    public static void drawBoxAtEntity(Entity entity, int red, int green, int blue, int alpha, float width, float height) {
        float x = MathUtils.getX(entity);
        float y = MathUtils.getY(entity) - height;
        float z = MathUtils.getZ(entity);
        drawBoxAt(x, y, z, red, green, blue, alpha, width, height);
    }

    public static void drawString(String var0, float var1, float var2, float var3, int var4, float var5, boolean var6) {
        float var7 = var5;
        RenderManager var8 = BadAddons.mc.getRenderManager();
        FontRenderer var9 = BadAddons.mc.fontRendererObj;
        Vector3f var10 = getRenderPos(var1, var2, var3);
        if (var6) {
            float var11 = var10.x * var10.x + var10.y * var10.y + var10.z * var10.z;
            float var12 = (float) Math.sqrt(var11);
            float var13 = var12 / 120.0F;
            var7 = var5 * 0.45F * var13;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        GL11.glPushMatrix();
        GL11.glTranslatef(var10.x, var10.y, var10.z);
        GL11.glRotatef(-var8.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(var8.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-var7, -var7, var7);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        int var14 = var9.getStringWidth(var0);
        var9.drawString(var0, -var14 / 2, 0, var4);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }

    public static void disableESP() {
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private static void renderBeaconBeam(double var0, double var2, double var4, int var6, float var7) {
        short var8 = 300;
        byte var9 = 0;
        int var10 = var9 + var8;
        Tessellator var11 = Tessellator.getInstance();
        WorldRenderer var12 = var11.getWorldRenderer();
        Minecraft.getMinecraft().getTextureManager().bindTexture(beaconBeam);
        GL11.glTexParameterf(3553, 10242, 10497.0F);
        GL11.glTexParameterf(3553, 10243, 10497.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double var13 = (float) Minecraft.getMinecraft().theWorld.getTotalWorldTime() + MathUtils.partialTicks;
        double var15 = MathHelper.func_181162_h(-var13 * 0.2 - (double) MathHelper.floor_double(-var13 * 0.1));
        float var17 = (float) (var6 >> 16 & 255) / 255.0F;
        float var18 = (float) (var6 >> 8 & 255) / 255.0F;
        float var19 = (float) (var6 & 255) / 255.0F;
        double var20 = var13 * 0.025 * -1.5;
        double var22 = 0.5 + Math.cos(var20 + 2.356194490192345) * 0.2;
        double var24 = 0.5 + Math.sin(var20 + 2.356194490192345) * 0.2;
        double var26 = 0.5 + Math.cos(var20 + 0.7853981633974483) * 0.2;
        double var28 = 0.5 + Math.sin(var20 + 0.7853981633974483) * 0.2;
        double var30 = 0.5 + Math.cos(var20 + 3.9269908169872414) * 0.2;
        double var32 = 0.5 + Math.sin(var20 + 3.9269908169872414) * 0.2;
        double var34 = 0.5 + Math.cos(var20 + 5.497787143782138) * 0.2;
        double var36 = 0.5 + Math.sin(var20 + 5.497787143782138) * 0.2;
        double var38 = -1.0 + var15;
        double var40 = (double) var8 * 2.5 + var38;
        var12.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        var12.pos(var0 + var22, var2 + (double) var10, var4 + var24).tex(1.0, var40).color(var17, var18, var19, var7).endVertex();
        var12.pos(var0 + var22, var2 + (double) var9, var4 + var24).tex(1.0, var38).color(var17, var18, var19, 1.0F).endVertex();
        var12.pos(var0 + var26, var2 + (double) var9, var4 + var28).tex(0.0, var38).color(var17, var18, var19, 1.0F).endVertex();
        var12.pos(var0 + var26, var2 + (double) var10, var4 + var28).tex(0.0, var40).color(var17, var18, var19, var7).endVertex();
        var12.pos(var0 + var34, var2 + (double) var10, var4 + var36).tex(1.0, var40).color(var17, var18, var19, var7).endVertex();
        var12.pos(var0 + var34, var2 + (double) var9, var4 + var36).tex(1.0, var38).color(var17, var18, var19, 1.0F).endVertex();
        var12.pos(var0 + var30, var2 + (double) var9, var4 + var32).tex(0.0, var38).color(var17, var18, var19, 1.0F).endVertex();
        var12.pos(var0 + var30, var2 + (double) var10, var4 + var32).tex(0.0, var40).color(var17, var18, var19, var7).endVertex();
        var12.pos(var0 + var26, var2 + (double) var10, var4 + var28).tex(1.0, var40).color(var17, var18, var19, var7).endVertex();
        var12.pos(var0 + var26, var2 + (double) var9, var4 + var28).tex(1.0, var38).color(var17, var18, var19, 1.0F).endVertex();
        var12.pos(var0 + var34, var2 + (double) var9, var4 + var36).tex(0.0, var38).color(var17, var18, var19, 1.0F).endVertex();
        var12.pos(var0 + var34, var2 + (double) var10, var4 + var36).tex(0.0, var40).color(var17, var18, var19, var7).endVertex();
        var12.pos(var0 + var30, var2 + (double) var10, var4 + var32).tex(1.0, var40).color(var17, var18, var19, var7).endVertex();
        var12.pos(var0 + var30, var2 + (double) var9, var4 + var32).tex(1.0, var38).color(var17, var18, var19, 1.0F).endVertex();
        var12.pos(var0 + var22, var2 + (double) var9, var4 + var24).tex(0.0, var38).color(var17, var18, var19, 1.0F).endVertex();
        var12.pos(var0 + var22, var2 + (double) var10, var4 + var24).tex(0.0, var40).color(var17, var18, var19, var7).endVertex();
        var11.draw();
        GlStateManager.disableCull();
        double var42 = -1.0 + var15;
        double var44 = (double) var8 + var42;
        var12.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        var12.pos(var0 + 0.2, var2 + (double) var10, var4 + 0.2).tex(1.0, var44).color(var17, var18, var19, 0.25F * var7).endVertex();
        var12.pos(var0 + 0.2, var2 + (double) var9, var4 + 0.2).tex(1.0, var42).color(var17, var18, var19, 0.25F).endVertex();
        var12.pos(var0 + 0.8, var2 + (double) var9, var4 + 0.2).tex(0.0, var42).color(var17, var18, var19, 0.25F).endVertex();
        var12.pos(var0 + 0.8, var2 + (double) var10, var4 + 0.2).tex(0.0, var44).color(var17, var18, var19, 0.25F * var7).endVertex();
        var12.pos(var0 + 0.8, var2 + (double) var10, var4 + 0.8).tex(1.0, var44).color(var17, var18, var19, 0.25F * var7).endVertex();
        var12.pos(var0 + 0.8, var2 + (double) var9, var4 + 0.8).tex(1.0, var42).color(var17, var18, var19, 0.25F).endVertex();
        var12.pos(var0 + 0.2, var2 + (double) var9, var4 + 0.8).tex(0.0, var42).color(var17, var18, var19, 0.25F).endVertex();
        var12.pos(var0 + 0.2, var2 + (double) var10, var4 + 0.8).tex(0.0, var44).color(var17, var18, var19, 0.25F * var7).endVertex();
        var12.pos(var0 + 0.8, var2 + (double) var10, var4 + 0.2).tex(1.0, var44).color(var17, var18, var19, 0.25F * var7).endVertex();
        var12.pos(var0 + 0.8, var2 + (double) var9, var4 + 0.2).tex(1.0, var42).color(var17, var18, var19, 0.25F).endVertex();
        var12.pos(var0 + 0.8, var2 + (double) var9, var4 + 0.8).tex(0.0, var42).color(var17, var18, var19, 0.25F).endVertex();
        var12.pos(var0 + 0.8, var2 + (double) var10, var4 + 0.8).tex(0.0, var44).color(var17, var18, var19, 0.25F * var7).endVertex();
        var12.pos(var0 + 0.2, var2 + (double) var10, var4 + 0.8).tex(1.0, var44).color(var17, var18, var19, 0.25F * var7).endVertex();
        var12.pos(var0 + 0.2, var2 + (double) var9, var4 + 0.8).tex(1.0, var42).color(var17, var18, var19, 0.25F).endVertex();
        var12.pos(var0 + 0.2, var2 + (double) var9, var4 + 0.2).tex(0.0, var42).color(var17, var18, var19, 0.25F).endVertex();
        var12.pos(var0 + 0.2, var2 + (double) var10, var4 + 0.2).tex(0.0, var44).color(var17, var18, var19, 0.25F * var7).endVertex();
        var11.draw();
        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
    }

    public static void drawScaledString(String var0, float var1, int var2, int var3, boolean var4) {
        GlStateManager.scale(var1, var1, var1);
        drawString(var0, (int) ((float) var2 / var1), (int) ((float) var3 / var1), var4);
    }

    public static void drawBoundingBoxAtPos(float var0, float var1, float var2, Color var3, float var4, float var5) {
        drawBoxAt(var0, var1, var2, var3.getRed(), var3.getGreen(), var3.getBlue(), var3.getAlpha(), var4, var5);
    }

    public static void drawSelectionBoundingBoxAtBlock(BlockPos var0, Color var1) {
        AxisAlignedBB var2 = BlockUtils.getAABBOfBlock(var0);
        if (var2 != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GL11.glLineWidth((float) 2);
            GlStateManager.color((float) var1.getRed() / 255.0F, (float) var1.getGreen() / 255.0F, (float) var1.getBlue() / 255.0F, (float) var1.getAlpha() / 255.0F);
            RenderGlobal.drawSelectionBoundingBox(var2);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glLineWidth((float) 2);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawStringAtRightUpOfDoubleChest(String var0) {
        ScaledResolution var1 = new ScaledResolution(Minecraft.getMinecraft());
        int var2 = (var1.getScaledWidth() + 176) / 2;
        int var3 = (var1.getScaledHeight() - 222) / 2;
        int var4 = var2 - 8 - RenderUtils.getStringWidth(var0);
        int var5 = var3 + 6;
        GL11.glTranslated(0.0, 0.0, 1.0);
        BadAddons.mc.fontRendererObj.drawString(var0, (float) var4, (float) var5, -1, false);
        GL11.glTranslated(0.0, 0.0, -1.0);
    }

    public static void renderBeaconBeam(BlockPos var0, int var1, float var2) {
        double var3 = (float) var0.getX() - MathUtils.getX(MinecraftUtils.getPlayer());
        double var5 = (float) var0.getY() - MathUtils.getY(MinecraftUtils.getPlayer());
        double var7 = (float) var0.getZ() - MathUtils.getZ(MinecraftUtils.getPlayer());
        renderBeaconBeam(var3, var5, var7, var1, var2);
    }

    public static void drawBoxAtBlock(BlockPos var0, Color var1, int var2, int var3, float var4) {
        drawBoxAtBlock(var0.getX(), var0.getY(), var0.getZ(), var1.getRed(), var1.getGreen(), var1.getBlue(), var1.getAlpha(), var2, var3, var4);
    }

    public static void drawLine(Vec3 var0, Vec3 var1, Color var2, int var3) {
        drawLine((float) var0.xCoord, (float) var0.yCoord, (float) var0.zCoord, (float) var1.xCoord, (float) var1.yCoord, (float) var1.zCoord, var2, var3);
    }

    public static void drawLineWithDepthRelative(float var0, float var1, float var2, float var3, float var4, float var5, Color var6, int var7) {
        GlStateManager.pushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth((float) var7);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        GlStateManager.color((float) var6.getRed(), (float) var6.getGreen(), (float) var6.getBlue());
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(var0, var1, var2).endVertex();
        worldRenderer.pos(var3, var4, var5).endVertex();
        tessellator.draw();
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GlStateManager.popMatrix();
    }

    public static void drawSelectionFilledBoxAtBlock(BlockPos var0, Color var1, int alpha) {
        AxisAlignedBB var2 = BlockUtils.getAABBOfBlock(var0);
        if (var2 != null) {
            drawFilledBoundingBoxRelative((float) var2.minX, (float) var2.minY, (float) var2.minZ, (float) var2.maxX, (float) var2.maxY, (float) var2.maxZ, var1.getRed(), var1.getGreen(), var1.getBlue(), alpha);
        }
    }

    private static void drawFilledBoundingBoxRelative(float var0, float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color((float) var6 / 255.0F, (float) var7 / 255.0F, (float) var8 / 255.0F, (float) var9 / 255.0F);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(var0, var1, var2).endVertex();
        worldRenderer.pos(var3, var1, var2).endVertex();
        worldRenderer.pos(var3, var1, var5).endVertex();
        worldRenderer.pos(var0, var1, var5).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(var0, var4, var5).endVertex();
        worldRenderer.pos(var3, var4, var5).endVertex();
        worldRenderer.pos(var3, var4, var2).endVertex();
        worldRenderer.pos(var0, var4, var2).endVertex();
        tessellator.draw();
        GlStateManager.color((float) var6 / 255.0F * 0.8F, (float) var7 / 255.0F * 0.8F, (float) var8 / 255.0F * 0.8F, (float) var9 / 255.0F);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(var0, var1, var5).endVertex();
        worldRenderer.pos(var0, var4, var5).endVertex();
        worldRenderer.pos(var0, var4, var2).endVertex();
        worldRenderer.pos(var0, var1, var2).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(var3, var1, var2).endVertex();
        worldRenderer.pos(var3, var4, var2).endVertex();
        worldRenderer.pos(var3, var4, var5).endVertex();
        worldRenderer.pos(var3, var1, var5).endVertex();
        tessellator.draw();
        GlStateManager.color((float) var6 / 255.0F * 0.9F, (float) var7 / 255.0F * 0.9F, (float) var8 / 255.0F * 0.9F, (float) var9 / 255.0F);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(var0, var4, var2).endVertex();
        worldRenderer.pos(var3, var4, var2).endVertex();
        worldRenderer.pos(var3, var1, var2).endVertex();
        worldRenderer.pos(var0, var1, var2).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(var0, var1, var5).endVertex();
        worldRenderer.pos(var3, var1, var5).endVertex();
        worldRenderer.pos(var3, var4, var5).endVertex();
        worldRenderer.pos(var0, var4, var5).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawRect(int var0, int var1, int var2, int var3, int var4) {
        Gui.drawRect(var1, var2, var1 + var3, var2 + var4, var0);
    }

    public static void drawBoxAtPos(float var0, float var1, float var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9) {
        drawFilledBoundingBoxAbsolute(var0 - var9, var1 - var9, var2 - var9, var0 + var7 + var9, var1 + var8 + var9, var2 + var7 + var9, var3, var4, var5, var6);
    }

    public static void drawString(String var0, int var1, int var2, boolean var3, Color var4) {
        String[] var5 = var0.split("\n");
        String[] var6 = var5;
        int var7 = var5.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            BadAddons.mc.fontRendererObj.drawString(var9, (float) var1, (float) var2, var4.getRGB(), var3);
            var2 += BadAddons.mc.fontRendererObj.FONT_HEIGHT + 1;
        }

    }

    public static Vector3f getRenderPos(float var0, float var1, float var2) {
        return new Vector3f(var0 - MathUtils.getX(BadAddons.mc.thePlayer), var1 - MathUtils.getY(BadAddons.mc.thePlayer), var2 - MathUtils.getZ(BadAddons.mc.thePlayer));
    }

    public static void drawOnSlot(int var0, int var1, int var2, int var3) {
        Vector2i var4 = getXYForSlot(var0, var1, var2);
        int var5 = var4.x;
        int var6 = var4.y;
        GL11.glTranslated(0.0, 0.0, 1.0);
        Gui.drawRect(var5, var6, var5 + 16, var6 + 16, var3);
        GL11.glTranslated(0.0, 0.0, -1.0);
    }

    private static Vector2i getXYForSlot(int var0, int var1, int var2) {
        ScaledResolution var3 = new ScaledResolution(Minecraft.getMinecraft());
        int var4 = (var3.getScaledWidth() - 176) / 2;
        int var5 = (var3.getScaledHeight() - 222) / 2;
        int var6 = var4 + var1;
        int var7 = var5 + var2 + (6 - (var0 - 36) / 9) * 9;
        return new Vector2i(var6, var7);
    }

    public static void drawStringCenteredScaledMaxWidth(
            String str,
            float x,
            float y,
            boolean shadow,
            int len,
            int colour
    ) {
        drawStringCenteredScaledMaxWidth(str, Minecraft.getMinecraft().fontRendererObj, x, y, shadow, len, colour);
    }

    @Deprecated
    public static void drawStringCenteredScaledMaxWidth(
            String str,
            FontRenderer fr,
            float x,
            float y,
            boolean shadow,
            int len,
            int colour
    ) {
        int strLen = fr.getStringWidth(str);
        float factor = len / (float) strLen;
        factor = Math.min(1, factor);
        int newLen = Math.min(strLen, len);

        float fontHeight = 8 * factor;

        drawStringScaled(str, x - newLen / 2, y - fontHeight / 2, shadow, colour, factor);
    }

    public static boolean isWithinRect(int x, int y, int topLeftX, int topLeftY, int width, int height) {
        return topLeftX <= x && x < topLeftX + width
                && topLeftY <= y && y < topLeftY + height;
    }

    public static void drawStringScaled(
            String str,
            float x,
            float y,
            boolean shadow,
            int colour,
            float factor
    ) {
        GlStateManager.scale(factor, factor, 1);
        BadAddons.mc.fontRendererObj.drawString(str, x / factor, y / factor, colour, shadow);
        GlStateManager.scale(1 / factor, 1 / factor, 1);
    }

    public static void drawScaledCenteredStringWithItems(String text, int x, int y, float scale) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);

        // Calculate initial centered X and Y positions for the text
        int textWidth = BadAddons.mc.fontRendererObj.getStringWidth(text.replaceAll("(:\\w+:)", "  ")); // Replace items with spaces to calculate width
        int centerX = Math.round(x / scale - textWidth / 2);
        int centerY = Math.round(y / scale - BadAddons.mc.fontRendererObj.FONT_HEIGHT / 2);

        int cursorX = centerX;
        String[] parts = text.split(" ");

        for (String part : parts) {
            if (part.startsWith(":") && part.endsWith(":")) {
                // Extract item/block name from placeholder
                String itemName = part.substring(1, part.length() - 1).toLowerCase();
                ItemStack itemStack = getItemStackByName(itemName);

                if (itemStack != null) {
                    // Render the item/block at the cursor position
                    renderScaledItemStack(itemStack, cursorX, centerY, scale);
                    cursorX += 18 / scale;  // Move cursor for item width (scaled)
                }
            } else {
                // Render the string part
                BadAddons.mc.fontRendererObj.drawStringWithShadow(part, cursorX, centerY, 1);
                cursorX += BadAddons.mc.fontRendererObj.getStringWidth(part) + 4; // Move cursor for text width + space
            }
        }

        GL11.glPopMatrix();
    }

    private static ItemStack getItemStackByName(String name) {
        ResourceLocation resourceLocation = new ResourceLocation(name);
        Item item = Item.itemRegistry.getObject(resourceLocation);
        if (item != null) {
            return new ItemStack(item);
        }

        return null;
    }

    private static void renderScaledItemStack(ItemStack stack, int x, int y, float scale) {
        GlStateManager.pushMatrix();

        // Adjust position to align with scaled items
        GlStateManager.translate(x, y - 4, 0);

        // Enable proper lighting to avoid darker appearance
        RenderHelper.enableGUIStandardItemLighting();

        // Scale item to maintain consistent size with text
        GlStateManager.scale(1.0F / scale, 1.0F / scale, 1.0F);

        // Render the item stack
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, 0, 0);

        // Disable lighting to restore default state
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }


    public static void drawBoxAt(double x, double y, double z, int red, int green, int blue, int alpha, float width, float height) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glLineWidth((float) 3);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderManager renderManager = BadAddons.mc.getRenderManager();
        GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);
        GlStateManager.color((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, (float) alpha / 255.0F);
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(x + width, y + height, z + width).endVertex();
        worldRenderer.pos(x + width, y + height, z - width).endVertex();
        worldRenderer.pos(x - width, y + height, z - width).endVertex();
        worldRenderer.pos(x - width, y + height, z + width).endVertex();
        worldRenderer.pos(x + width, y + height, z + width).endVertex();
        worldRenderer.pos(x + width, y, z + width).endVertex();
        worldRenderer.pos(x + width, y, z - width).endVertex();
        worldRenderer.pos(x - width, y, z - width).endVertex();
        worldRenderer.pos(x - width, y, z + width).endVertex();
        worldRenderer.pos(x - width, y, z - width).endVertex();
        worldRenderer.pos(x - width, y + height, z - width).endVertex();
        worldRenderer.pos(x - width, y, z - width).endVertex();
        worldRenderer.pos(x + width, y, z - width).endVertex();
        worldRenderer.pos(x + width, y + height, z - width).endVertex();
        worldRenderer.pos(x + width, y, z - width).endVertex();
        worldRenderer.pos(x + width, y, z + width).endVertex();
        worldRenderer.pos(x - width, y, z + width).endVertex();
        worldRenderer.pos(x - width, y + height, z + width).endVertex();
        worldRenderer.pos(x + width, y + height, z + width).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glPopMatrix();
    }

    public static void drawFullBlockAt(double x, double y, double z, Color color) {
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;
        float a = color.getAlpha() / 255.0F;

        drawBoxAt((float) (x-0.5), (float) (y-0.5), (float) (z-0.5), (int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255), 0.5f, 0.5f);
    }
    
    public static void drawNameAndLevel(FontRenderer var0, String var1, String var2, int var3, int var4, double var5, double var7) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) var3, (float) var4, 1.0F);
        GlStateManager.scale(var5, var5, 1.0);
        var0.drawString(var1, 1.0F, 1.0F, -1, true);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (var3 + 16 - MathUtils.ceil((double) RenderUtils.getStringWidth(var2) * var7)), (float) (var4 + 16 - MathUtils.ceil((double) RenderUtils.getStringHeight(var2) * var7)), 1.0F);
        GlStateManager.scale(var7, var7, 1.0);
        var0.drawString(var2, 0.0F, 0.0F, -1, true);
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    public static void enableESP() {
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
    }

    public static void drawBoundingBox(AxisAlignedBB var0, int var1, Color var2) {
        EntityPlayerSP var3 = MinecraftUtils.getPlayer();
        float var4 = MathUtils.getX(var3);
        float var5 = MathUtils.getY(var3);
        float var6 = MathUtils.getZ(var3);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-var4, -var5, -var6);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth((float) var1);
        RenderGlobal.drawOutlinedBoundingBox(var0, var2.getRed(), var2.getGreen(), var2.getBlue(), var2.getAlpha());
        GlStateManager.translate(var4, var5, var6);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static void drawLineWithDepthAbsolute(float var0, float var1, float var2, float var3, float var4, float var5, Color var6, int var7) {
        EntityPlayerSP var8 = MinecraftUtils.getPlayer();
        float var9 = MathUtils.getX(var8);
        float var10 = MathUtils.getY(var8);
        float var11 = MathUtils.getZ(var8);
        var0 -= var9;
        var1 -= var10;
        var2 -= var11;
        var3 -= var9;
        var4 -= var10;
        var5 -= var11;
        drawLineWithDepthRelative(var0, var1, var2, var3, var4, var5, var6, var7);
    }

    public static void drawFilledFace(BlockUtils.Face var0, Color var1) {
        drawFilledBoundingBoxAbsolute((float) var0.sx, (float) var0.sy, (float) var0.sz, (float) var0.tx, (float) var0.ty, (float) var0.tz, var1.getRed(), var1.getGreen(), var1.getBlue(), var1.getAlpha());
    }

    public static void drawBoundingBoxAtBlock(BlockPos var0, Color var1) {
        drawBoxAt((float) var0.getX() + 0.5F, (float) var0.getY(), (float) var0.getZ() + 0.5F, var1.getRed(), var1.getGreen(), var1.getBlue(), var1.getAlpha(), 0.5F, 1F);
    }

    public static void drawBoundingBoxAtBlock(BlockPos var0, Color var1, float size) {
        drawBoxAt((float) var0.getX() + 0.5F, (float) var0.getY(), (float) var0.getZ() + 0.5F, var1.getRed(), var1.getGreen(), var1.getBlue(), var1.getAlpha(), size/2, size);
    }

    public static void drawTexture(ResourceLocation var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
    	BadAddons.mc.getTextureManager().bindTexture(var0);
        GlStateManager.color(255.0F, 255.0F, 255.0F);
        Gui.drawModalRectWithCustomSizedTexture(var1, var2, (float) var7, (float) var8, var3, var4, (float) var5, (float) var6);
    }

    public static void drawLine(float var0, float var1, float var2, float var3, float var4, float var5, Color var6, int var7) {
        drawLineWithDepthAbsolute(var0, var1, var2, var3, var4, var5, var6, var7);
    }

    public static void drawBoxAtBlock(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, float var9) {
        drawFilledBoundingBoxAbsolute((float) var0 - var9, (float) var1 - var9, (float) var2 - var9, (float) (var0 + var7) + var9, (float) (var1 + var8) + var9, (float) (var2 + var7) + var9, var3, var4, var5, var6);
    }

    public static void drawFilledBoxAtEntity(Entity var0, int var1, int var2, int var3, int var4, float var5, float var6, float var7) {
        float var8 = MathUtils.getX(var0);
        float var9 = MathUtils.getY(var0) - var7 * var6;
        float var10 = MathUtils.getZ(var0);
        drawFilledBoundingBoxAbsolute(var8 - var5, var9, var10 - var5, var8 + var5, var9 + var6, var10 + var5, var1, var2, var3, var4);
    }
    
    
    public static void drawRoundedRect(int left, int top, int right, int bottom, Color color, int radiusInDegrees) {
        int radius = (int) (Math.tan(Math.toRadians(radiusInDegrees / 2.0)) * (right - left) / 2.0);

        Gui.drawRect(left + radius, top, right - radius, bottom, color.getRGB());
        Gui.drawRect(left, top + radius, left + radius, bottom - radius, color.getRGB());
        Gui.drawRect(right - radius, top + radius, right, bottom - radius, color.getRGB());

        drawArc(left + radius, top + radius, radius, 0, 90, color.getRGB());
        drawArc(right - radius, top + radius, radius, 270, 360, color.getRGB());
        drawArc(left + radius, bottom - radius, radius, 90, 180, color.getRGB());
        drawArc(right - radius, bottom - radius, radius, 180, 270, color.getRGB());
    }

    public static void drawArc(int x, int y, int radius, int startAngle, int endAngle, int color) {
        for (int i = startAngle; i <= endAngle; i++) {
            int xPos = x + (int) (radius * Math.cos(Math.toRadians(i)));
            int yPos = y + (int) (radius * Math.sin(Math.toRadians(i)));
            Gui.drawRect(xPos, yPos, xPos + 1, yPos + 1, color);
        }
    }

    public static List<ItemStack> getSentItemsFromTrade(ContainerChest chest) {
        return getItemsInSquare(chest, 1, 1, 4, 4);
    }

    public static void sendMouseClick(int windowId, int slotIndex) {
        EntityPlayerSP playerIn = Minecraft.getMinecraft().thePlayer;
        short short1 = playerIn.openContainer.getNextTransactionID(playerIn.inventory);
        ItemStack itemstack = playerIn.openContainer.getSlot(slotIndex).getStack();
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C0EPacketClickWindow(
                windowId,
                slotIndex,
                0,
                0,
                itemstack,
                short1
        ));
    }

    public static List<ItemStack> getReceivedItemsFromTrade(ContainerChest chest) {
        return getItemsInSquare(chest, 6, 1, 9, 4);
    }

    public static void drawItemStackWithText(ItemStack stack, int x, int y, String text) {
        if (stack == null) return;
        RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.zLevel = -145; //Negates the z-offset of the below method.
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, x, y, text);
        itemRender.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
    }

    public static List<ItemStack> getItemsInSquare(ContainerChest gui, int startX, int startY, int endX, int endY) {
        List<ItemStack> items = new ArrayList<>();
        int chestWidth = 9; // The width of the chest, always 9 slots per row

        // Loop through the specified grid coordinates to collect items
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                int slotIndex = (y - 1) * chestWidth + (x - 1); // Calculate the slot index based on coordinates
                ItemStack itemStack = gui.getSlot(slotIndex).getStack();

                if (itemStack != null) {
                    items.add(itemStack); // Add the non-empty item to the list
                }
            }
        }
        return items;
    }

    public static void fillSlots(Container container, int startX, int startY, int endX, int endY, ItemStack stack) {
        int chestWidth = 9;

        // Iterate through the specified grid coordinates
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                // Calculate the slot index based on grid coordinates
                int slotIndex = (y - 1) * chestWidth + (x - 1);

                // Get the slot from the container
                Slot slot = container.getSlot(slotIndex);

                // Set the slot's stack to the fillStack
                slot.putStack(stack);
            }
        }
    }
    public static boolean isSlotContainsItem(Container container, int x, int y, Item item) {
        int chestWidth = 9; // The width of the chest GUI, typically 9 slots per row

        // Convert 1-based coordinates to 0-based index
        int slotIndex = ((y - 1) * chestWidth) + (x - 1);

        // Check if the index is within the bounds of the container's slots
        if (slotIndex < 0 || slotIndex >= container.inventorySlots.size()) {
            return false; // Slot index out of bounds
        }

        // Get the slot from the container
        Slot slot = container.getSlot(slotIndex);
        ItemStack itemStack = slot.getStack();

        // Check if the item stack is not empty and matches the item
        return itemStack != null && itemStack.getItem() == item;
    }
    /**
     * Checks if a given slot is within the specified rectangular area in the container.
     *
     * @param slot The slot to check.
     * @param container The container where the slot is located.
     * @param startX The starting X coordinate (1-based index).
     * @param startY The starting Y coordinate (1-based index).
     * @param endX The ending X coordinate (1-based index).
     * @param endY The ending Y coordinate (1-based index).
     * @return True if the slot is within the specified area, false otherwise.
     */
    public static boolean isSlotWithin(Slot slot, Container container, int startX, int startY, int endX, int endY) {
        int chestWidth = 9;
        int slotIndex = slot.slotNumber;
        if (slotIndex == -1) {
            return false; // Slot not found in the container
        }

        // Convert slot index back to grid coordinates
        int x = (slotIndex % chestWidth) + 1; // Convert 0-based index to 1-based coordinate
        int y = (slotIndex / chestWidth) + 1; // Convert 0-based index to 1-based coordinate

        // Check if the coordinates are within the specified rectangular area
        return x >= startX && x <= endX && y >= startY && y <= endY;
    }

    public static ItemStack getItemStack(int x, int y, GuiChest chest) {
        return chest.inventorySlots.getSlot((y - 1) * 9 + (x - 1)).getStack();
    }

    public static void drawListString(int x, int y, String s, List<ItemStack> items) {
        BadAddons.mc.fontRendererObj.drawStringWithShadow(s, x, y, 1);

        int yOffset = 12;
        for (int i = 0; i < items.size(); i++) {
            BadAddons.mc.fontRendererObj.drawStringWithShadow("§7" + (i + 1) + ". §b" + items.get(i).getDisplayName(), x, y + yOffset, 1);
            yOffset += 10;
        }
    }

    public static void drawListStrings(int x, int y, List<String> lines) {
        int yOffset = 12;
        for (int i = 0; i < lines.size(); i++) {
            BadAddons.mc.fontRendererObj.drawStringWithShadow(lines.get(i), x, y + yOffset, 1);
            yOffset += 10;
        }
    }

    public static void drawSmallBoundingBoxAtBlock(BlockPos var0, Color var1) {
        drawBoxAt((float) var0.getX() + 0.5F, (float) var0.getY(), (float) var0.getZ() + 0.5F, var1.getRed(), var1.getGreen(), var1.getBlue(), var1.getAlpha(), 0.25F, 0.5F);

    }

    public static void drawCustomBoundingBoxAtBlock(BlockPos var0, Color var1, float size) {
        drawBoxAt((float) var0.getX() + size/2, (float) var0.getY(), (float) var0.getZ() + size/2, var1.getRed(), var1.getGreen(), var1.getBlue(), var1.getAlpha(), size/2, size);

    }


}
