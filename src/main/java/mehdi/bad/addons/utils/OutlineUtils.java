/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.entity.Entity
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 */
package mehdi.bad.addons.utils;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Events.RenderEntityModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class OutlineUtils {
    public static void outlineEntity(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scaleFactor, Color color, int outlineWidth) {
        float gamma = BadAddons.mc.gameSettings.gammaSetting;
        GlStateManager.disableDepth();
        BadAddons.mc.gameSettings.gammaSetting = Float.MAX_VALUE;
        GlStateManager.resetColor();
        OutlineUtils.setColor(color);
        GlStateManager.disableDepth();
        OutlineUtils.renderOne(outlineWidth);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        OutlineUtils.setColor(color);
        OutlineUtils.renderTwo();
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        OutlineUtils.setColor(color);
        OutlineUtils.renderThree();
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        OutlineUtils.setColor(color);
        OutlineUtils.renderFour(color);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        OutlineUtils.setColor(color);
        OutlineUtils.renderFive();
        OutlineUtils.setColor(Color.WHITE);
        GlStateManager.enableDepth();
        BadAddons.mc.gameSettings.gammaSetting = gamma;
    }

    public static void outlineEntity(RenderEntityModelEvent event, Color color, int outlineWidth) {
        OutlineUtils.outlineEntity(event.model, (Entity)event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.headYaw, event.headPitch, event.scaleFactor, color, outlineWidth);
    }

    public static void drawFillEntity(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scaleFactor, Color color) {
        // Save the original OpenGL state
    	int originalBlendFuncSrc = GL11.glGetInteger(GL11.GL_BLEND_SRC);
    	int originalBlendFuncDst = GL11.glGetInteger(GL11.GL_BLEND_DST);
    	boolean originalBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
    	boolean originalTextureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
    	float originalLineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);

        // Render the model with the filled outline
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glPopMatrix();


        // Restore the original OpenGL state
        GL11.glLineWidth(originalLineWidth);
        if (originalBlendEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glBlendFunc(originalBlendFuncSrc, originalBlendFuncDst);
        if (originalTextureEnabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
    }

    public static void drawFillEntity(RenderEntityModelEvent event, Color color) {
    	OutlineUtils.drawFillEntity(event.model, (Entity)event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.headYaw, event.headPitch, event.scaleFactor, color);
    }
    
    
    private static void renderOne(float lineWidth) {
        OutlineUtils.checkSetupFBO();
        GL11.glPushAttrib((int)1048575);
        GL11.glDisable((int)3008);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)lineWidth);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2960);
        GL11.glClear((int)1024);
        GL11.glClearStencil((int)15);
        GL11.glStencilFunc((int)512, (int)1, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    private static void renderTwo() {
        GL11.glStencilFunc((int)512, (int)0, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    private static void renderThree() {
        GL11.glStencilFunc((int)514, (int)1, (int)15);
        GL11.glStencilOp((int)7680, (int)7680, (int)7680);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    private static void renderFour(Color color) {
        OutlineUtils.setColor(color);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)10754);
        GL11.glPolygonOffset((float)1.0f, (float)-2000000.0f);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
    }

    private static void renderFive() {
        GL11.glPolygonOffset((float)1.0f, (float)2000000.0f);
        GL11.glDisable((int)10754);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)2960);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glEnable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)3008);
        GL11.glPopAttrib();
    }

    private static void setColor(Color color) {
        GL11.glColor4d((double)((float)color.getRed() / 255.0f), (double)((float)color.getGreen() / 255.0f), (double)((float)color.getBlue() / 255.0f), (double)((float)color.getAlpha() / 255.0f));
    }

    private static void checkSetupFBO() {
        Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        if (fbo != null && fbo.depthBuffer > -1) {
            OutlineUtils.setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    private static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT((int)fbo.depthBuffer);
        int stencilDepthBufferId = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT((int)36161, (int)stencilDepthBufferId);
        EXTFramebufferObject.glRenderbufferStorageEXT((int)36161, (int)34041, (int)BadAddons.mc.displayWidth, (int)BadAddons.mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36128, (int)36161, (int)stencilDepthBufferId);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36096, (int)36161, (int)stencilDepthBufferId);
    }
}

