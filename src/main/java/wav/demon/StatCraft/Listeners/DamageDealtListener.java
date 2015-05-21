package wav.demon.StatCraft.Listeners;

import com.mysema.query.QueryException;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import wav.demon.StatCraft.Magic.EntityCode;
import wav.demon.StatCraft.Querydsl.DamageDealt;
import wav.demon.StatCraft.Querydsl.QDamageDealt;
import wav.demon.StatCraft.StatCraft;

import java.util.UUID;

public class DamageDealtListener implements Listener {

    private StatCraft plugin;

    public DamageDealtListener(StatCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageDealt(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();
        if (damager instanceof Player) {
            final UUID uuid = damager.getUniqueId();
            final int damageDealt = (int) Math.round(event.getFinalDamage());

            if (damagee instanceof LivingEntity) {
                final LivingEntity entity = (LivingEntity) event.getEntity();

                plugin.getThreadManager().schedule(DamageDealt.class, new Runnable() {
                    @Override
                    public void run() {
                        int id = plugin.getDatabaseManager().getPlayerId(uuid);

                        QDamageDealt d = QDamageDealt.damageDealt;

                        // For special entities which are clumped together
                        // currently only skeletons and wither skeletons fall under this category
                        EntityCode code = EntityCode.fromEntity(entity);

                        String entityValue;
                        if (entity instanceof Player) {
                            entityValue = String.valueOf(plugin.getDatabaseManager().getPlayerId(entity.getUniqueId()));
                        } else {
                            entityValue = entity.getName();
                        }

                        try {
                            // INSERT
                            SQLInsertClause clause = plugin.getDatabaseManager().getInsertClause(d);

                            if (clause == null)
                                return;

                            clause.columns(d.id, d.entity, d.type, d.amount)
                                .values(id, entityValue, code.getCode(), damageDealt).execute();
                        } catch (QueryException e) {
                            // UPDATE
                            SQLUpdateClause clause = plugin.getDatabaseManager().getUpdateClause(d);

                            if (clause == null)
                                return;

                            clause.where(
                                d.id.eq(id),
                                d.entity.eq(entityValue),
                                d.type.eq(code.getCode())
                            ).set(d.amount, d.amount.add(damageDealt)).execute();
                        }
                    }
                });
            }
        }
    }
}
