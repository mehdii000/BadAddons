package mehdi.bad.addons.Features.General;

import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Events.GuiContainerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class HighlightGraceItems {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Color WHITE_COLOR = Color.WHITE;
    private static final Color CYAN_COLOR = Color.CYAN;

    @SubscribeEvent
    public void onDrawSlot(GuiContainerEvent.DrawSlotEvent event) {
        if (!Configs.HighlightGraceItems) return;
        Slot slot = event.slot;
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips)
                    .stream()
                    .map(StringUtils::stripControlCodes)
                    .filter(uncolored -> uncolored.contains("Can buy in:"))
                    .findFirst()
                    .ifPresent(uncolored -> {
                        String duration = uncolored.split("Can buy in: ")[1];
                        drawColoredRect(slot.xDisplayPosition, slot.yDisplayPosition, duration.contains("Soon!") ? WHITE_COLOR : CYAN_COLOR);
                    });
        }
    }

    private void drawColoredRect(int x, int y, Color color) {
        Gui.drawRect(x, y, x + 16, y + 16, color.getRGB());
    }
}