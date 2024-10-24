package mehdi.bad.addons.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraft.client.Minecraft;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;

public class UpdateCheck {

    private static final String UPDATE_CHECK_URL = "https://api.github.com/repos/mehdii000/BadAddons/releases/latest";
    public static boolean isUpdating = false;
    public static boolean shouldUpdate = false;

    public static void checkForUpdates() {
        try {
            URL url = new URL(UPDATE_CHECK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                String jsonResponse = response.toString();
                JsonParser jsonParser = new JsonParser();
                JsonObject releaseJson = jsonParser.parse(jsonResponse).getAsJsonObject();
                String latestVersion = releaseJson.get("tag_name").getAsString();
                if (latestVersion.startsWith("v") && latestVersion.split("v")[1].equalsIgnoreCase(BadAddons.VERSION)) {
                    ChatLib.chat("§e[BA] No update was found!");
                } else {
                    shouldUpdate = true;
                    BadAddons.mc.thePlayer.playSound("random.orb", 0.7f, 1f);
                    //ChatLib.chat("§e[BA] New update was found! BadAddons " + latestVersion + " §c§l[CLICK TO UPDATE]");
                    ChatLib.sendHoverMessage("§7[BadAddons] Update §e" + latestVersion + " §7was found! §c[CLICK TO UPDATE]", "/bb updatemod", "Update to the latest version!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            ChatLib.chat("§c[BadAddons] Error checking for updates!");
        }
    }

    public static void downloadAndExtractMod() {
        new Thread(() -> {
            InputStream inputStream = null;
            InputStream trustStoreInputStream;

            try {
                // Load truststore from the classpath
                trustStoreInputStream = BadAddons.class.getResourceAsStream("/skytilscacerts.jks");
                if (trustStoreInputStream == null) {
                    throw new FileNotFoundException("Truststore file not found");
                }

                // Create and load the truststore
                KeyStore trustStore = KeyStore.getInstance("JKS");
                trustStore.load(trustStoreInputStream, "skytilsontop".toCharArray());

                // Initialize TrustManagerFactory with the loaded truststore
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);

                // Initialize SSLContext with the trust managers from TrustManagerFactory
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

                // Set the default SSL socket factory
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                // Fetch information about the latest release
                URL apiUrl = new URL(UPDATE_CHECK_URL);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON response
                    JsonObject releaseJson = new JsonParser().parse(response.toString()).getAsJsonObject();
                    String latestVersion = releaseJson.get("tag_name").getAsString();

                    // Extract URL of the jar file from the assets array
                    JsonArray assets = releaseJson.get("assets").getAsJsonArray();
                    String jarUrl = null;
                    for (int i = 0; i < assets.size(); i++) {
                        JsonObject asset = assets.get(i).getAsJsonObject();
                        if (asset.get("name").getAsString().equals("BadAddons-" + latestVersion.split("v")[1] + ".jar")) {
                            jarUrl = asset.get("browser_download_url").getAsString();
                            break;
                        }
                    }

                    if (jarUrl != null) {
                        File modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
                        File oldModFile = new File(modsFolder, "BadAddons-" + BadAddons.VERSION + ".jar");

                        // Check and delete the old mod file if it exists
                        if (oldModFile.exists()) {
                            oldModFile.deleteOnExit(); // Mark the file for deletion when JVM exits
                            ChatLib.chat("§eOld BadAddons version " + BadAddons.VERSION + " will be deleted when the game exits.");
                        }

                        // Download the new JAR file
                        try {
                            inputStream = new URL(jarUrl).openStream();
                            File jarFile = new File(modsFolder, "BadAddons-" + latestVersion.split("v")[1] + ".jar");

                            // Create parent directories if they don't exist
                            File parentDir = new File(jarFile.getParent());
                            if (!parentDir.exists()) {
                                parentDir.mkdirs();
                            }

                            // Write the downloaded JAR file
                            try (FileOutputStream fileOutputStream = new FileOutputStream(jarFile)) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, bytesRead);
                                }
                            }

                            ChatLib.chat("§aSuccessfully downloaded BadAddons version " + latestVersion + ". §cPlease restart your game for changes to take effect.");

                        } catch (IOException e) {
                            e.printStackTrace();
                            ChatLib.chat("§cFailed to download the new version of BadAddons.");
                        }

                    } else {
                        ChatLib.chat("§cError! BadAddons JAR not found in the latest update assets!");
                    }
                } else {
                    ChatLib.chat("§cError downloading BadAddons latest update!");
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start(); // Run everything in a new thread to keep the process smooth
    }


}
