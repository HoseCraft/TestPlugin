package com.infermc.test;

import com.infermc.hosecraft.command.CommandInterface;
import com.infermc.hosecraft.command.CommandSource;
import com.infermc.hosecraft.server.Player;

public class trustedManager implements CommandInterface {
    TestPlugin parent;
    public trustedManager(TestPlugin plugin) {
        this.parent = plugin;
    }

    @Override
    public boolean run(CommandSource commandSource, String[] strings) {
        if (parent.isTrusted(commandSource.getName()) || commandSource.isOperator()) {
            if (strings.length > 0) {
                String u = strings[0];

                if (!parent.isTrusted(u)) {
                    if (parent.isOnline(u)) {
                        Player p = parent.getOnlinePlayer(u);
                        p.sendMessage("&a" + commandSource.getName() + " added you to the trusted list.");
                        u = p.getName();
                    }
                    parent.addTrusted(strings[0]);
                    commandSource.sendMessage("&aAdded " + u + " to the trusted list.");
                } else {
                    commandSource.sendMessage("&cThat player is already on the trust list!");
                }
            } else {
                commandSource.sendMessage("Syntax: /trust username");
            }
            return true;
        }
        // Deny existence of command!
        return false;
    }
}
