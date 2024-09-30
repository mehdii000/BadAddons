package mehdi.bad.addons.Guis;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Features.Lowballing.TradesTracker;
import mehdi.bad.addons.utils.GuiUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.List;

public class LastTradeGUI extends GuiScreen {

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        // Separate sent and received items
        List<ItemStack> sentItems = TradesTracker.currentSentItems;
        List<ItemStack> receivedItems = TradesTracker.currentReceivedItems;

        if (sentItems.isEmpty() && receivedItems.isEmpty()) {
            GuiUtils.drawStringCenteredScaledMaxWidth("§cNo Past Trade Yet!", this.width/2, this.height/2, false, 40, 1);
            return;
        }

        int gridSize = 3; // Number of items per row
        int itemSize = 16; // Size of each item
        int spacing = 10; // Spacing between items

        // Set positions for sent and received sections
        int startXSent = 120; // Start drawing sent items from the right
        int startYSent = 75;
        int startXReceived = width - 120 - (gridSize * itemSize); // Start drawing received items from the left
        int startYReceived = 75;

        // Calculate the total value for sent items and draw them
        for (int i = 0; i < sentItems.size(); i++) {
            int x = startXSent + (i % gridSize) * (itemSize + spacing); // Calculate X position
            int y = startYSent + (i / gridSize) * (itemSize + spacing); // Calculate Y position

            ItemStack itemStack = sentItems.get(i);

            // Check if the mouse is hovering over this item
            if (mouseX >= x && mouseX < x + itemSize && mouseY >= y && mouseY < y + itemSize) {
                // Draw the item name above the item stack
                //GuiUtils.drawString(itemStack.getDisplayName(), (this.width/2) - (itemStack.getDisplayName().length()*3)/2, this.height/3, true);
                GuiUtils.drawListStrings((this.width/2) - (itemStack.getDisplayName().length()*3)/2, this.height/3, itemStack.getTooltip(BadAddons.mc.thePlayer, false));
            }

            GuiUtils.drawItemStackWithText(itemStack, x, y, "" + itemStack.stackSize);
        }

        // Calculate the total value for received items and draw them
        for (int i = 0; i < receivedItems.size(); i++) {
            int x = startXReceived + (i % gridSize) * (itemSize + spacing); // Calculate X position
            int y = startYReceived + (i / gridSize) * (itemSize + spacing); // Calculate Y position

            ItemStack itemStack = receivedItems.get(i);

            // Check if the mouse is hovering over this item
            if (mouseX >= x && mouseX < x + itemSize && mouseY >= y && mouseY < y + itemSize) {
                // Draw the item name above the item stack
                //GuiUtils.drawString(itemStack.getDisplayName(), (this.width/2) - (itemStack.getDisplayName().length()*3)/2, this.height/3, true);
                GuiUtils.drawListStrings((this.width/2) - (itemStack.getDisplayName().length()*3)/2, this.height/3, itemStack.getTooltip(BadAddons.mc.thePlayer, false));
            }

            GuiUtils.drawItemStackWithText(itemStack, x, y, "" + itemStack.stackSize);
        }

        // Draw the total value for sent and received sections above their respective items
        this.drawString(this.fontRendererObj, "§cSent:", startXSent, startYSent - 40, 0xFFFFFF);
        this.drawString(this.fontRendererObj, "§aReceived:", startXReceived, startYReceived - 40, 0xFFFFFF);
    }

    // Helper method to format large numbers with suffixes
    private String formatLargeNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format("%.1fb", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format("%.1fm", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000.0);
        } else {
            return Long.toString(number);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
