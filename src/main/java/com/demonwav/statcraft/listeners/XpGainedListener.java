/*
 * StatCraft Bukkit Plugin
 *
 * Copyright (c) 2015 Kyle Wood (DemonWav)
 * http://demonwav.com
 *
 * MIT License
 */

package com.demonwav.statcraft.listeners;

import com.demonwav.statcraft.StatCraft;
import com.demonwav.statcraft.querydsl.QXpGained;
import com.demonwav.statcraft.querydsl.XpGained;

import com.mysema.query.QueryException;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.UUID;

public class XpGainedListener implements Listener {

    private StatCraft plugin;

    public XpGainedListener(StatCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onXpGain(PlayerExpChangeEvent event) {
        final int amount = event.getAmount();
        if (amount > 0) {
            final UUID uuid = event.getPlayer().getUniqueId();

            plugin.getThreadManager().schedule(XpGained.class, new Runnable() {
                @Override
                public void run() {
                    int id = plugin.getDatabaseManager().getPlayerId(uuid);

                    QXpGained x = QXpGained.xpGained;

                    try {
                        // INSERT
                        SQLInsertClause clause = plugin.getDatabaseManager().getInsertClause(x);

                        if (clause == null)
                            return;

                        clause.columns(x.id, x.amount).values(id, amount).execute();
                    } catch (QueryException e) {
                        // UPDATE
                        SQLUpdateClause clause = plugin.getDatabaseManager().getUpdateClause(x);

                        if (clause == null)
                            return;

                        clause.where(x.id.eq(id)).set(x.amount, x.amount.add(amount)).execute();
                    }
                }
            });
        }
    }
}