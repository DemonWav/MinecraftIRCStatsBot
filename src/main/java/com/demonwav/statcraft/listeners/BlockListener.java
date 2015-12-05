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
import com.demonwav.statcraft.Util;
import com.demonwav.statcraft.querydsl.BlockBreak;
import com.demonwav.statcraft.querydsl.BlockPlace;
import com.demonwav.statcraft.querydsl.QBlockBreak;
import com.demonwav.statcraft.querydsl.QBlockPlace;

import com.mysema.query.QueryException;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class BlockListener implements Listener {

    private StatCraft plugin;

    public BlockListener(StatCraft plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final short blockid = (short) event.getBlock().getTypeId();
        final short damage = Util.damageValue(blockid, event.getBlock().getData());
        final UUID uuid = event.getPlayer().getUniqueId();

        plugin.getThreadManager().schedule(BlockBreak.class, new Runnable() {
            @Override
            public void run() {
                int id = plugin.getDatabaseManager().getPlayerId(uuid);

                QBlockBreak b = QBlockBreak.blockBreak;

                try {
                    // INSERT
                    SQLInsertClause clause = plugin.getDatabaseManager().getInsertClause(b);

                    if (clause == null)
                        return;

                    clause.columns(b.id, b.blockid, b.damage, b.amount)
                        .values(id, blockid, damage, 1).execute();
                } catch (QueryException e) {
                    // UPDATE
                    SQLUpdateClause clause = plugin.getDatabaseManager().getUpdateClause(b);

                    if (clause == null)
                        return;

                    clause.where(
                        b.id.eq(id),
                        b.blockid.eq(blockid),
                        b.damage.eq(damage)
                    ).set(b.amount, b.amount.add(1)).execute();
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final short blockid = (short) event.getBlock().getTypeId();
        final short damage = Util.damageValue(blockid, event.getBlock().getData());
        final UUID uuid = event.getPlayer().getUniqueId();

        plugin.getThreadManager().schedule(BlockPlace.class, new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                int id = plugin.getDatabaseManager().getPlayerId(uuid);

                QBlockPlace b = QBlockPlace.blockPlace;

                try {
                    // INSERT
                    SQLInsertClause clause = plugin.getDatabaseManager().getInsertClause(b);

                    if (clause == null)
                        return;

                    clause.columns(b.id, b.blockid, b.damage, b.amount)
                        .values(id, blockid, damage, 1).execute();
                } catch (QueryException e) {
                    // UPDATE
                    SQLUpdateClause clause = plugin.getDatabaseManager().getUpdateClause(b);

                    if (clause == null)
                        return;

                    clause.where(
                        b.id.eq(id),
                        b.blockid.eq(blockid),
                        b.damage.eq(damage)
                    ).set(b.amount, b.amount.add(1)).execute();
                }
            }
        });
    }
}