package mehdi.bad.addons.utils;

import mehdi.bad.addons.BadAddons;
import net.minecraft.entity.monster.EntityMagmaCube;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KuudraUtils {

    public static boolean matchesFreshRegex(String str) {
        String regex = "Party > .* (\\w+): FRESH(?:ED.*|\\s\\(.*\\)|.*)?";
        return str.matches(regex);
    }

    public static String extractName(String str) {
        String regex = "Party > .* (\\w+): FRESH(?:ED.*|\\s\\(.*\\)|.*)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.matches()) {
            return matcher.group(1); // Extract the username
        }
        return null;
    }

    public static float getHP() {
        List<EntityMagmaCube> cubes = BadAddons.mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityMagmaCube)
                .map(entity -> (EntityMagmaCube) entity)
                .filter(cube -> cube.getSlimeSize() >= 25)
                .collect(Collectors.toList());

        // Calculate total HP of all selected cubes, mapped to a new range (1000 HP to 240)
        float totalHP = 0;
        for (EntityMagmaCube cube : cubes) {
            float mappedHP = cube.getHealth() * 0.24f;  // Scale HP to a range where 1000 maps to 240
            totalHP += mappedHP;
        }

        return totalHP;  // Return the total mapped HP
    }

    public static int getLastPhaseMaxtHP() {
        return 240;
    }


    public static String getHitsHP() {
        List<EntityMagmaCube> cubes = BadAddons.mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityMagmaCube)
                .map(entity -> (EntityMagmaCube) entity)
                .filter(cube -> cube.getSlimeSize() >= 25)
                .collect(Collectors.toList());

        for (EntityMagmaCube cube : cubes) {
            if (cube.posY < 30 || cube.posY > 71) {
                return "§e" + (cube.getHealth() / 100000 * 100);
            }
        }

        return "§cNaN";
    }
}
