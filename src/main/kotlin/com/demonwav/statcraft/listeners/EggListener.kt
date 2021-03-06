/*
 * StatCraft Bukkit Plugin
 *
 * Copyright (c) 2016 Kyle Wood (DemonWav)
 * https://www.demonwav.com
 *
 * MIT License
 */

package com.demonwav.statcraft.listeners

import com.demonwav.statcraft.StatCraft
import com.demonwav.statcraft.magic.ProjectilesCode
import com.demonwav.statcraft.querydsl.QProjectiles
import com.mysema.query.types.expr.CaseBuilder
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerEggThrowEvent

class EggListener(private val plugin: StatCraft) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEggHit(event: PlayerEggThrowEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val worldName = player.world.name
        val eggLocation = event.egg.location
        val playerLocation = player.location
        val hatched = event.isHatching
        val numberHatched = event.numHatches

        val distance = playerLocation.distance(eggLocation)
        val finalDistance = Math.round(distance * 100.0).toInt()

        val code = if (hatched && numberHatched == 1.toByte()) {
            ProjectilesCode.HATCHED_EGG
        } else if (hatched) {
            ProjectilesCode.FOUR_HATCHED_EGG
        } else {
            ProjectilesCode.UNHATCHED_EGG
        }

        plugin.threadManager.schedule<QProjectiles>(
            uuid, worldName,
            { p, clause, id, worldId ->
                clause.columns(p.id, p.worldId, p.type, p.amount, p.totalDistance, p.maxThrow)
                    .values(id, worldId, code.code, 1, finalDistance, finalDistance).execute()
            }, { p, clause, id, worldId ->
                clause.where(p.id.eq(id), p.worldId.eq(worldId), p.type.eq(code.code))
                    .set(p.totalDistance, p.totalDistance.add(finalDistance))
                    .set(
                        p.maxThrow,
                        CaseBuilder()
                            .`when`(p.maxThrow.lt(finalDistance))
                            .then(finalDistance)
                            .otherwise(p.maxThrow))
                    .execute()
            }
        )
    }
}
