package mehdi.bad.addons.utils;

import mehdi.bad.addons.BadAddons;
import net.minecraft.entity.monster.EntityMagmaCube;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KuudraUtils {

    public static boolean matchesFreshRegex(String str) {
        String regex = "Party > .* (\\w+): FRESH(?:ED.*|\\s\\(.*\\))?\n";
        return str.matches(regex);
    }

    // Function to extract the [name] from the string
    public static String extractName(String str) {
        String regex = "Party > .* (\\w+): FRESH(?:ED.*|\\s\\(.*\\))?\n";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public static float getHP() {
        List<EntityMagmaCube> cubes = BadAddons.mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityMagmaCube)
                .map(entity -> (EntityMagmaCube) entity)
                .filter(cube -> cube.getSlimeSize() == 15)
                .collect(Collectors.toList());

        // Calculate total HP of all selected cubes, mapped to a new range (1000 HP to 300)
        float totalHP = 0;
        for (EntityMagmaCube cube : cubes) {
            float mappedHP = cube.getHealth() * 0.3f;  // Scale HP to a range where 1000 maps to 300
            totalHP += mappedHP;
        }

        return totalHP;  // Return the total mapped HP
    }

    public static float geMaxtHP() {
        List<EntityMagmaCube> cubes = BadAddons.mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityMagmaCube)
                .map(entity -> (EntityMagmaCube) entity)
                .filter(cube -> cube.getSlimeSize() == 15)
                .collect(Collectors.toList());

        // Calculate total HP of all selected cubes, mapped to a new range (1000 HP to 300)
        float totalHP = 0;
        for (EntityMagmaCube cube : cubes) {
            float mappedHP = cube.getMaxHealth() * 0.3f;  // Scale HP to a range where 1000 maps to 300
            totalHP += mappedHP;
        }

        return totalHP;  // Return the total mapped HP
    }


}
