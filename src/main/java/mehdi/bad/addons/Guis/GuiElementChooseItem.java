package mehdi.bad.addons.Guis;

import mehdi.bad.addons.utils.GuiUtils;
import mehdi.bad.addons.utils.MathUtils;
import mehdi.bad.addons.utils.RenderUtils;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiElementChooseItem {

    private final int ROW_HEIGHT = 20;

    private boolean focus =  false;
    private String selectedItem;
    private List<String> toChooseFrom = new ArrayList<String>();

    public GuiElementChooseItem(String... items) {
        this.focus = false;
        toChooseFrom = Arrays.asList(items);
        this.selectedItem = toChooseFrom.get(0);
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (focus) {

        }
        if (GuiUtils.isWithinRect(mouseX, mouseY, x, y, width, height)) {
            focus = true;
        } else {
            focus = false;
        }
    }

    private int RowHeightSmoothing = 0;

    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        Gui.drawRect(x-1, y-1, x+width+1, y+height+1, Color.WHITE.getRGB());
        Gui.drawRect(x, y, x+width, y+height, Color.BLACK.getRGB());
        RenderUtils.drawScaledCenteredString("§6" + selectedItem, x+(width/2), y+(height/2), 1);

        RowHeightSmoothing = MathUtils.intLerp(RowHeightSmoothing, focus ? (ROW_HEIGHT) : 0, 0.1f);
        if (focus) {
            Gui.drawRect(x-1, y+height-1+10, x+width+1, 10+y+height+(RowHeightSmoothing*toChooseFrom.size())+1, Color.WHITE.getRGB());
            Gui.drawRect(x, y+height+10, x+width, 10+y+height+(RowHeightSmoothing*toChooseFrom.size()), Color.BLACK.getRGB());

            for (int i = 0; i < toChooseFrom.size(); i++) {
                RenderUtils.drawScaledCenteredString((GuiUtils.isWithinRect(mouseX, mouseY, x, y+(ROW_HEIGHT*i), width, ROW_HEIGHT/2) ?
                                "§a" : "§f") + toChooseFrom.get(i), x+(width/2), 4+y+(ROW_HEIGHT/2)+(height)+(RowHeightSmoothing*i), 1);
            }
        }

    }

}
