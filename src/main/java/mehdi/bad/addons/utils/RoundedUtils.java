package mehdi.bad.addons.utils;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;

public class RoundedUtils {
	
	public static void drawRoundedRect(float x, float y, float x1, float y1, float radius, int color) {
	    GL11.glPushMatrix();
	    GL11.glScaled(0.5D, 0.5D, 0.5D);
	    x *= 2.0D;
	    y *= 2.0D;
	    x1 *= 2.0D;
	    y1 *= 2.0D;
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_LINE_SMOOTH);
	    GL11.glColor4f(((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f, ((color >> 24) & 0xFF) / 255f);
	    GL11.glBegin(GL11.GL_POLYGON);
	    int i;
	    for (i = 0; i <= 90; i += 3)
	        GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
	    for (i = 90; i <= 180; i += 3)
	        GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
	    for (i = 0; i <= 90; i += 3)
	        GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
	    for (i = 90; i <= 180; i += 3)
	        GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
	    GL11.glEnd();
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_LINE_SMOOTH);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_LINE_SMOOTH);
	    GL11.glScaled(2.0D, 2.0D, 2.0D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glPopMatrix();
	    GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
	}
	
}
