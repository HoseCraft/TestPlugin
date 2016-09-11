package com.infermc.test;

import com.infermc.hosecraft.command.Command;
import com.infermc.hosecraft.events.EventHandler;
import com.infermc.hosecraft.events.Listener;
import com.infermc.hosecraft.events.chat.ChatEvent;
import com.infermc.hosecraft.events.player.PlayerJoinEvent;
import com.infermc.hosecraft.events.player.PlayerQuitEvent;
import com.infermc.hosecraft.plugins.JavaPlugin;
import com.infermc.hosecraft.server.Player;
import com.infermc.hosecraft.util.Chat;
import com.infermc.hosecraft.wrappers.ConfigSection;
import com.infermc.hosecraft.wrappers.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestPlugin extends JavaPlugin implements Listener {
    List<String> trustedPlayers = new ArrayList<String>();
    File playersFile;

    @Override
    public void onLoad() {
        // Register custom level generator
        // The Level Generator must NOT rely on anything from a started server and must only use logs
        // or functions that will always be accessible and guaranteed to work.
        getServer().registerLevelGenerator(new FlatLevelGenerator(getLogger()));
        getServer().registerLevelGenerator(new FudgiesLevelGenerator(getLogger()));
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        BlockHandler bk = new BlockHandler(this);

        playersFile = new File(getDataFolder()+"/trusted.yml");
        if (getServer() != null) {
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getPluginManager().registerEvents(bk, this);
            getLogger().info("I've been enabled. I'm running on HoseCraft v" + getServer().getVersion() + "-" + getServer().getFlavour());
        }

        messageCommand msgCMD = new messageCommand(getServer(),this);
        Command cmd = new Command("msg",msgCMD);
        getServer().getCommandRegistry().registerCommand(this,cmd);

        getServer().getCommandRegistry().registerCommand(this,new Command("trust",new trustedManager(this)).setDescription("Adds a player to the trusted players list."));
        loadTrusted();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        ev.setMessage("&0[&a+&0] &7Hello, &8"+ev.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        ev.setMessage("&0[&c-&0] &7Goodbye, &8"+ev.getPlayer().getName());
    }

    @EventHandler
    public void onChat(ChatEvent ev) {
        ev.setCancelled(true);

        String name = ev.getSource().getDisplayname();
        if (ev.getSource().isOperator()) {
            name = "&4[&cO&4] &c"+name+" &4";
        } else {
            if (!isTrusted(ev.getSource())) {
                name = "&2[&aG&2] &a" + name + " &2";
            } else {
                name = "&9[&1T&9] &9" + name + " &1";
            }
        }
        String chatMessage = name+"> &f"+ev.getMessage();
        getServer().getLogger().info(Chat.stripColour(chatMessage));
        List<String> msgs = wordWrap(chatMessage,64);
        for (Player p : getServer().getPlayers()) {
            for (String m : msgs) p.sendMessage(m);
        }
    }

    public void addTrusted(Player p) {
        addTrusted(p.getName());
    }
    public void addTrusted(String username) {
        if (!trustedPlayers.contains(username.toLowerCase())) {
            trustedPlayers.add(username.toLowerCase());
            saveTrusted();
        }
    }

    public void removeTrusted(Player p) {
        removeTrusted(p.getName());
    }
    public void removeTrusted(String username) {
        if (trustedPlayers.contains(username.toLowerCase())) {
            trustedPlayers.remove(username.toLowerCase());
            saveTrusted();
        }
    }

    public boolean isTrusted(Player p) {
        return isTrusted(p.getName());
    }
    public boolean isTrusted(String username) {
        if (trustedPlayers.contains(username.toLowerCase())) {
            return true;
        }
        return false;
    }

    public void saveTrusted() {
        YamlConfiguration cfg = new YamlConfiguration();
        try {
            ConfigSection root = cfg.getRoot();
            root.set("trusted",this.trustedPlayers);
            cfg.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> loadTrusted() {
        YamlConfiguration yml = new YamlConfiguration();
        if (playersFile.exists()) {
            try {
                InputStream stream = new FileInputStream(playersFile);
                yml.load(stream);
                ConfigSection root = yml.getRoot();
                List<String> obj = (ArrayList<String>) root.get("trusted");
                this.trustedPlayers = obj;
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isOnline(String username) {
        if (getOnlinePlayer(username) != null) return true;
        return false;
    }

    public Player getOnlinePlayer(String username) {
        for (Player p : getServer().getPlayers()) {
            if (p.getName().equalsIgnoreCase(username)) return p;
        }
        return null;
    }

    public List<String> wordWrap(String input, int maxLineLength) {
        String[] words = input.split(" ");
        List<String> output = new ArrayList<String>();
        String sc = "";
        for (String word : words) {
            if ((sc.length()+1 + word.length()) > maxLineLength) {
                output.add(sc.trim());
                sc = word;
            } else {
                sc = sc +" "+ word;
            }
        }
        if (sc != "") output.add(sc.trim());
        return output;
    }
}