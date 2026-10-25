package com.onxon.antiblockrotation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("anti_block_rotation.json");

    public static Config INSTANCE = new Config();

    public boolean enabled = true;
    // Empty = leave the original line alone. Supports &-colour codes (e.g. &a, &l).
    public String name = "";
    public String balance = "";
    public String shards = "";
    public String kills = "";
    public String deaths = "";
    public String playtime = "";

    /** Order matches the sidebar top to bottom. */
    public String[] lines() {
        return new String[]{balance, shards, kills, deaths, playtime};
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader r = Files.newBufferedReader(FILE)) {
                    Config c = GSON.fromJson(r, Config.class);
                    if (c != null) INSTANCE = c;
                }
            }
        } catch (Exception e) {
            System.err.println("[Anti block rotation] Could not read config: " + e);
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(INSTANCE, w);
        } catch (Exception e) {
            System.err.println("[Anti block rotation] Could not save config: " + e);
        }
    }
}
