package mehdi.bad.addons.Guis;

import mehdi.bad.addons.Config.modules.MovableConfigManager;
import mehdi.bad.addons.Config.modules.MovableModule;
import mehdi.bad.addons.Config.modules.MovablesManager;
import mehdi.bad.addons.utils.GuiUtils;
import mehdi.bad.addons.utils.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiEditorGui extends GuiScreen {

    private boolean isDragging = false, isResizing = false;
    private MovableModule selectedModule = null;
    private int offsetX, offsetY;

    // Define size for the resize handle in the bottom-right corner
    private static final int RESIZE_HANDLE_SIZE = 10;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        // Draw each module at its current position
        for (MovableModule module : MovablesManager.modules) {
            int x = module.getX();
            int y = module.getY();
            int width = module.getWidth();
            int height = module.getHeight();

            // Draw module background
            //drawRect(x, y, x + width, y + height, selectedModule == module ? Color.CYAN.getRGB() : Color.GRAY.getRGB());
            GuiUtils.drawTexture(new ResourceLocation("badaddons:uibox.png"), x, y, width, height);
            GuiUtils.drawTexture(new ResourceLocation("badaddons:select.png"), x + width - RESIZE_HANDLE_SIZE, y + height - RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);

            module.render();
            RenderUtils.drawScaledCenteredString("§b" + module.getName(), x + (width / 2), y, 1.25f - ((float) 1 / width));
        }

        // Handle dragging
        if (isDragging && selectedModule != null) {
            selectedModule.setPosition(mouseX - offsetX, mouseY - offsetY);
        }
        // Handle resizing
        else if (isResizing && selectedModule != null) {
            int newWidth = mouseX - selectedModule.getX();
            int newHeight = mouseY - selectedModule.getY();

            if (selectedModule.shouldKeepRatio()) {
                float aspectRatio = (float) selectedModule.getOriginalWidth() / selectedModule.getOriginalHeight();
                newHeight = (int) (newWidth / aspectRatio); // Adjust height to maintain aspect ratio
            }

            // Ensure minimum dimensions are respected
            selectedModule.setDimensions(Math.max(40, newWidth), Math.max(14, newHeight));
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) { // Left-click
            for (MovableModule module : MovablesManager.modules) {
                int x = module.getX();
                int y = module.getY();
                int width = module.getWidth();
                int height = module.getHeight();

                // Check if the click is inside the resize handle
                if (mouseX >= x + width - RESIZE_HANDLE_SIZE && mouseX <= x + width && mouseY >= y + height - RESIZE_HANDLE_SIZE && mouseY <= y + height) {
                    isResizing = true;
                    selectedModule = module;
                    return; // No need to check for dragging if resizing
                }

                // Check if the click is inside the module but outside the resize handle (for dragging)
                if (mouseX >= x && mouseX <= x + width - RESIZE_HANDLE_SIZE && mouseY >= y && mouseY <= y + height - RESIZE_HANDLE_SIZE) {
                    isDragging = true;
                    selectedModule = module;
                    offsetX = mouseX - x;
                    offsetY = mouseY - y;
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) { // Left mouse button released
            isDragging = false;
            isResizing = false;
            selectedModule = null;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onGuiClosed() {
        // Save the current positions and dimensions of the modules
        MovableConfigManager.save();
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }
}