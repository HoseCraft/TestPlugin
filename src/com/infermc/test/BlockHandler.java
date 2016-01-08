package com.infermc.test;

import com.infermc.hosecraft.events.EventHandler;
import com.infermc.hosecraft.events.Listener;
import com.infermc.hosecraft.events.block.BlockBreakEvent;
import com.infermc.hosecraft.events.block.BlockPlaceEvent;

public class BlockHandler implements Listener {
    TestPlugin parent;

    public BlockHandler(TestPlugin p) {
        this.parent = p;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent ev) {
        if (!ev.getPlayer().isOperator() && !parent.isTrusted(ev.getPlayer())) {
            ev.setCancelled(true);
            ev.getPlayer().sendMessage("&cGuest's aren't allow to break blocks.");
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent ev) {
        int block = ev.getBlock().id;
        if (block == 8 || block == 9 || block == 10 || block == 11) {
            ev.setCancelled(true);
            ev.getPlayer().sendMessage("&cLiquid placement is disabled.");
        } else {
            if (!ev.getPlayer().isOperator() && !parent.isTrusted(ev.getPlayer())) {
                ev.setCancelled(true);
                ev.getPlayer().sendMessage("&cGuest's aren't allow to place blocks.");
            }
        }
    }
}
