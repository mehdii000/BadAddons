package mehdi.bad.addons.Objects;

import mehdi.bad.addons.BadAddons;
import net.minecraft.client.settings.KeyBinding;

public class KeyBind {

    private final KeyBinding keyBinding;

    public KeyBind(String name, int key) {
        this.keyBinding = new KeyBinding(name, key, BadAddons.MODID);
        KeyBindUtils.addKeyBind(this);
    }

    public boolean isKeyDown() {
        return this.keyBinding.isKeyDown();
    }

    public boolean isPressed() {
        return this.keyBinding.isPressed();
    }

    public KeyBinding mcKeyBinding() {
        return this.keyBinding;
    }
}
