package mehdi.bad.addons.Guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BedsFlipperSettingsGUI extends GuiScreen {

    private final int NAMEBAR_WIDTH = 200;

    private List<GuiElementTextField> textFields = new ArrayList<>();

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 16, 98, 20, "Add Element"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (!textFields.isEmpty()) {
            for (int i = 0; i < textFields.size(); i++) {
                textFields.get(i).render(this.width / 2 - (NAMEBAR_WIDTH/2), 32 + (24*i));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        //textField.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        /*TextField
        if (GuiUtils.isWithinRect(
                mouseX,
                mouseY,
                (this.width / 2) - (SEARCH_WIDTH/2),
                32,
                SEARCH_WIDTH,
                20
        )) {
            textField.mouseClicked(mouseX, mouseY, mouseButton);
        } else {
            textField.setFocus(false);
        }*/
    }
}
