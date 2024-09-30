package mehdi.bad.addons.Events;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class GuiContainerEvent extends Event {
    public final GuiContainer gui;
    public final Container container;

    public GuiContainerEvent(GuiContainer gui, Container container) {
        this.gui = gui;
        this.container = container;
    }

    public static class DrawSlotEvent extends GuiContainerEvent {
        public final Slot slot;

        public DrawSlotEvent(GuiContainer gui, Container container, Slot slot) {
            super(gui, container);
            this.slot = slot;
        }
    }
}