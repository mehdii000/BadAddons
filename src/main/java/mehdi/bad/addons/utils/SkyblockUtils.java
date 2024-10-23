package mehdi.bad.addons.utils;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Objects.ScoreBoard;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SkyblockUtils {
	
	public static EntityArmorStand isArmorStandNearby(Entity entity, String armorStandName) {
	    double radius = 1; // Change this to adjust the search radius
	    double heightRad = 2;
	    List<EntityArmorStand> armorStands = entity.worldObj.getEntitiesWithinAABB(EntityArmorStand.class,
	            new AxisAlignedBB(entity.posX - radius, entity.posY - heightRad, entity.posZ - radius,
	                    entity.posX + radius, entity.posY + entity.height + heightRad, entity.posZ + radius));
	    if (armorStands.isEmpty() || armorStands == null) return null;
	    for (EntityArmorStand armorStand : armorStands) {
	        if (armorStand.hasCustomName() && StringUtils.stripControlCodes(armorStand.getCustomNameTag()).contains(armorStandName)) {
	            return armorStand;
	        }
	    }
	    return null;
	}
	
	public static boolean isOnIsland(String islandName) {
	    Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
	    ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);

	    if (sidebar != null) {
	        for (String line : scoreboard.getSortedScores(sidebar).stream()
	                .map(Score::getPlayerName)
	                .collect(Collectors.toList())) {
	            if (line.contains(islandName)) {
	                return true;
	            }
	        }
	    }

	    return false;
	}
	
	public static String getRegexInSidebar(String regex) {
		Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
	    ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);

	    if (sidebar != null) {
	        for (String line : scoreboard.getSortedScores(sidebar).stream()
	                .map(Score::getPlayerName)
	                .collect(Collectors.toList())) {
	            if (line.contains(regex)) {
	                return line;
	            }
	        }
	    }

	    return null;
	}
	
	public static String getIsland() {
		Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
	    ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
	    if (sidebar != null) {
	        if (scoreboard.getSortedScores(sidebar).stream()
	                .map(Score::getPlayerName)
	                .collect(Collectors.toList()).get(5).contains("⏣")) {
	        	return scoreboard.getSortedScores(sidebar).stream()
		                .map(Score::getPlayerName)
		                .collect(Collectors.toList()).get(5);
	        }
	    }
	    return "null";
	}

		private static final ArrayList<String> m7ScoreBoards = new ArrayList<>(Arrays.asList(
				"Soul Dragon", "Ice Dragon", "Apex Dragon", "Flame Dragon", "Power Dragon", "No Alive Dragons"
		));

		private static final ArrayList<String> netherMaps = new ArrayList<>(Arrays.asList(
				"Barbarian Outpost", "The Bastion", "Blazing Volcano", "Burning Desert",
				"Cathedral", "Crimson Fields", "Dojo", "Dragontail", "Forgotten Skull",
				"Kuudra's Hollow", "Mage Outpost", "Magma Chamber", "Mystic Marsh",
				"Odger's Hut", "Ruins of Ashfang", "Stronghold", "The Wasteland", "Crimson Isle"
		));

		private static final ArrayList<String> maps = new ArrayList<>(Arrays.asList(
				"Ruins", "Forest", "Mountain", "High Level", "Wilderness",
				"Dungeon Hub", "Kuudra's Hollow", "Deep Caverns",
				"Hub", "Spider's Den", "Gunpowder Mines", "Void Sepulture", "Dragon's Nest",
				"The End", "The Mist", "Blazing Fortress", "The Catacombs", "Howling Cave",
				"Your Island", "Dojo Arena", "None"
		));

		private static final ArrayList<String> dwarvenMaps = new ArrayList<>(Arrays.asList(
				"The Forge", "Forge Basin", "Palace Bridge", "Royal Palace", "Aristocrat Passage",
				"Hanging Court", "Divan's Gateway", "Far Reserve", "Goblin Burrows",
				"Miner's Guild", "Great Ice Wall", "The Mist", "C&",
				"Grand Library", "Barracks of Heroes", "Dwarven Village", "The Lift",
				"Royal Quarters", "Lava Springs", "Cliffside Veins", "Rampart's Quarry",
				"Upper Mines", "Royal Mines", "Dwarven Mines", "Gates to the Mines"
		));
		private static final ArrayList<String> crystalHollowsMaps = new ArrayList<>(Arrays.asList(
				"Fairy Grotto",
				"Goblin Holdout", "Goblin Queen's Den",
				"Jungle", "Jungle Temple",
				"Mines of Divan", "Mithril Deposits",
				"Lost Precursor City", "Precursor Remnants",
				"Magma Fields", "Crystal Nucleus",
				"Khazad", "Dragon's Lair"
		));

		public static String currentMap = "";
		private static boolean isInCrystalHollows = false;
		private static boolean isInDwarven = false;
		private static boolean isInNether = false;
		private static String dungeon = "F6";

		public static String updateCurrentMap() {
			ArrayList<String> lines = ScoreBoard.getLines();
			if (lines.size() < 5) return "";
			String line = lines.get(lines.size() - 3) + lines.get(lines.size() - 4) + lines.get(lines.size() - 5);
			StringBuilder removeSkippingChar = new StringBuilder();
			line = ChatLib.removeFormatting(line);
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '\'' || c == ' '
						|| c == '(' || c == ')' || c == 'û' || c == '&')
					removeSkippingChar.append(c);
			}
			line = removeSkippingChar.toString();

			for (String str : m7ScoreBoards) {
				if (line.contains(str)) {
					dungeon = "M7";
					return "The Catacombs";
				}
			}

			String result = "Others";
			for (String map : maps) {
				if (line.contains(map)) {
					result = map;
					break;
				}
			}
			isInCrystalHollows = false;
			if (result.equals("Others")) {
				for (String map : crystalHollowsMaps) {
					if (line.contains(map)) {
						result = map;
						isInCrystalHollows = true;
					}
				}
			}

			isInDwarven = result.equals("The Mist");
			if (result.equals("Others")) {
				for (String map : dwarvenMaps) {
					if (line.contains(map)) {
						result = map;
						isInDwarven = true;
					}
				}
			}

			if (result.equals("Others") || result.equals("Ruins")) {
				for (String map : netherMaps) {
					if (line.contains(map)) {
						result = map;
						isInNether = true;
					}
				}
			}

			if (result.equals("The Catacombs")) {
				Pattern pattern = Pattern.compile("The Catacombs \\((.*)\\)");
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					dungeon = matcher.group(1);
				}
			}
			return result;
		}

		public static String getDungeon() {
			return dungeon;
		}

		public static void setDungeon(String dungeon1) {
			dungeon = dungeon1;
		}

		public static String getCurrentMap() {
			return currentMap;
		}

		public static void setCurrentMap(String map) {
			currentMap = map;
		}

		public static boolean isInKuudra() {
			return currentMap.equals("Kuudra's Hollow");
		}

		public static boolean isInSpiderDen() {
			return currentMap.equals("Spider's Den");
		}

		public static boolean isInDungeon() {
			return currentMap.contains("The Catacombs");
		}

		public static boolean isInMist() {
			return currentMap.equals("The Mist");
		}

		public static boolean isInDragon() {
			return currentMap.equals("Dragon's Nest");
		}

		public static boolean isInTheEnd() {
			return currentMap.equals("The End");
		}

		public static boolean isInVoidSepulture() {
			return currentMap.equals("Void Sepulture");
		}

		public static boolean isInEndIsland() {
			return isInDragon() || isInTheEnd() || isInVoidSepulture();
		}

		public static boolean isInGunpowderMines() {
			return currentMap.equals("Gunpowder Mines") || currentMap.equals("Deep Caverns");
		}

		public static boolean isInHowlingCave() {
			return currentMap.equals("Howling Cave");
		}

		public static boolean isInRuin() {
			return currentMap.equals("Ruins");
		}

		public static boolean isInMountain() {
			return currentMap.equals("Mountain");
		}

		public static boolean isInForest() {
			return currentMap.equals("Forest");
		}

		public static boolean isInCrystalHollows() {
			return isInCrystalHollows;
		}

		public static boolean isInDojo() {
			return currentMap.equals("Dojo Arena");
		}

		public static boolean isInDwarven() {
			return isInDwarven;
		}

		public static boolean isInSkyblock() {
			return ChatLib.removeFormatting(ScoreBoard.title).contains("SKYBLOCK");
		}

		public static boolean isInNether() {
			return isInNether;
		}

	public static void multipleDings() {
		Timer timer = new Timer();

		// Play first sound immediately
		BadAddons.mc.thePlayer.playSound("random.orb", 0.7f, 1f);

		// Schedule the second sound after 1000 milliseconds (1 second)
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				BadAddons.mc.thePlayer.playSound("random.orb", 0.8f, 0.9f);
			}
		}, 750);

		// Schedule the third sound after an additional 500 milliseconds (1.5 seconds total)
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				BadAddons.mc.thePlayer.playSound("random.orb", 0.9f, 0.9f);
			}
		}, 500);

		// Timer doesn't need to be explicitly shut down in this case
	}
}
