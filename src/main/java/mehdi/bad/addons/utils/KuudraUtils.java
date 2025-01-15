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

        for (EntityMagmaCube cube : cubes) {
            return Math.round(cube.getHealth()) / 100f;
        }
        return 250;
    }

    public static int getLastPhaseMaxtHP() {
        return 250;
    }


    public static String getHitsHP() {
        List<EntityMagmaCube> cubes = BadAddons.mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityMagmaCube)
                .map(entity -> (EntityMagmaCube) entity)
                .filter(cube -> cube.getSlimeSize() >= 25)
                .collect(Collectors.toList());

        for (EntityMagmaCube cube : cubes) {
            if (cube.posY < 30 || cube.posY > 71) {
                return mapColorToHP(
                        (int) cube.getHealth() - 25000,
                        75000,
                        "§a",
                        "§e",
                        "§c",
                        "§4"
                ) + (float) Math.round(((cube.getHealth() - 25000) / 75000.0f * 100) * 100) / 100;
            }
        }

        return "§cNaN";
    }

    public static String mapColorToHP(int health, int maxHealth, String... mappers) {
        // Ensure there are at least 4 mappers
        if (mappers.length < 4) {
            throw new IllegalArgumentException("At least 4 color mappers are required.");
        }

        // Calculate thresholds based on the maximum health
        int quarter = maxHealth / 4;

        // Map colors based on the calculated thresholds
        if (health > 3 * quarter) {
            return mappers[0]; // High health
        } else if (health > 2 * quarter) {
            return mappers[1]; // Medium-high health
        } else if (health > quarter) {
            return mappers[2]; // Medium-low health
        } else {
            return mappers[3]; // Low health
        }
    }

}
