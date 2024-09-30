package mehdi.bad.addons.Config.modules;

import mehdi.bad.addons.Features.General.CombatXPTracker;
import mehdi.bad.addons.Features.Kuudra.KuudraHandler;
import mehdi.bad.addons.Features.Maps.CrimsonIslesMap;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class MovablesManager {
    public static List<MovableModule> modules = new ArrayList<>();

    public static void registerModule(MovableModule module) {
        MinecraftForge.EVENT_BUS.register(module);
        modules.add(module);
    }

    public static void loadModules() {
        registerModule(new CombatXPTracker());
        registerModule(new CrimsonIslesMap());
        registerModule(new KuudraHandler());
    }

    public static void renderModules() {
        modules.forEach(MovableModule::render);
    }
}
