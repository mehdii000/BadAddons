package mehdi.bad.addons.Config.modules;

import mehdi.bad.addons.Features.Dungeons.catacombs.DungeonManager;
import mehdi.bad.addons.Features.Kuudra.KuudraHandler;
import mehdi.bad.addons.Features.Kuudra.PreSupplyDetection;
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
        registerModule(new CrimsonIslesMap());
        registerModule(new KuudraHandler());
        registerModule(new PreSupplyDetection());
        registerModule(new DungeonManager());
    }

    public static void renderModules() {
        modules.forEach(MovableModule::render);
    }
}
