package mehdi.bad.addons.Mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

public class MixinLoader implements IFMLLoadingPlugin {
    public MixinLoader() {
        System.out.println("[BadAddons] Mixin Init!");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.badaddons.json");
    }

    public String getModContainerClass() {
        return null;
    }

    public void injectData(Map var1) {
    }

    public String getAccessTransformerClass() {
        return null;
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Nullable
    public String getSetupClass() {
        return null;
    }
}
