package mehdi.bad.addons.Features.Maps;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Config.modules.MovableModule;
import mehdi.bad.addons.utils.GuiUtils;
import mehdi.bad.addons.utils.MinecraftUtils;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.util.ResourceLocation;

public class CrimsonIslesMap extends MovableModule {

	public CrimsonIslesMap() {
		// Pass name, initial position, and dimensions to the superclass
		super("CrimsonIslesMap", 16, 16, 160, 128, true); // Initial width 160, height 128
	}

	@Override
	public void render() {
		if (!Configs.CrimsonIslesMap) return;
		if (MinecraftUtils.getWorld().provider.getDimensionId() != -1) return;

		if (SkyblockUtils.isInNether()) {
			// Draw the Crimson Isles map texture at the module's current position and size
			GuiUtils.drawTexture(new ResourceLocation("badaddons:maps/crimsonislesmap.png"), getX(), getY(), getWidth(), getHeight());

			// Calculate the scaling factor based on the current size of the module
			float scaleX = (float) getWidth() / 160f;
			float scaleY = (float) getHeight() / 128f;

			// Calculate the pointer position relative to the new scaled map size
			int pointerX = (int) ((Math.abs(-752 - BadAddons.mc.thePlayer.getPosition().getX()) / 5.25f) * scaleX);
			int pointerY = (int) ((Math.abs(-1088 - BadAddons.mc.thePlayer.getPosition().getZ()) / 5.25f) * scaleY);

			// Draw a rotated player icon at the scaled pointer position
			GuiUtils.drawRotatedTexture(new ResourceLocation("badaddons:GreenPlayerIcon.png"), getX() + pointerX, getY() + pointerY, 8, 8, (int) MinecraftUtils.getPlayer().rotationYaw + 180);
		}
	}
}