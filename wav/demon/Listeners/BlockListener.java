package wav.demon.Listeners;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import wav.demon.StatCraft;
import wav.demon.StatTypes;

import java.util.ArrayList;
import java.util.Collection;

public class BlockListener extends StatListener {

    public BlockListener(StatCraft plugin) {
        super(plugin);
    }

    @SuppressWarnings({"unused", "deprecation"})
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        String message = event.getBlock().getType().getId() + ":" + event.getBlock().getData();
        final String name = event.getPlayer().getName();
        incrementStat(StatTypes.BLOCK_BREAK.id, name, message);

        if (plugin.getMined_ores()) {
            Block block = event.getBlock();
            Material type = block.getType();
            Collection<ItemStack> drops = block.getDrops(event.getPlayer().getItemInHand());
            for (ItemStack stack : drops) {

                message = type.toString();

                if (message != null) {
                    for (int x = 1; x <= stack.getAmount(); x++) {
                        incrementStat(StatTypes.MINED.id, name, message);
                    }
                }

            }
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        final String message = event.getBlock().getType().getId() + ":" + event.getBlock().getData();
        final String name = event.getPlayer().getName();
        incrementStat(StatTypes.BLOCK_PLACE.id, name, message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("blocks")) {
            // list the number of recorded deaths for a player
            ArrayList<String> names = getPlayers(sender, args);
            if (names == null)
                return false;

            for (String name : names) {
                String blocksBroken = df.format(super.getStat(name, StatTypes.BLOCK_BREAK.id));
                String blocksPlaced = df.format(super.getStat(name, StatTypes.BLOCK_PLACE.id));

                String message = "§c" + name + "§f - Blocks Broken: " + blocksBroken + " Blocks Placed: " + blocksPlaced;

                // print out the results
                respondToCommand(message, args, sender);
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("mined")) {
            // list ores mined by a player
            if (args.length > 3 || args.length <= 1)
                return false;

            String name = args[0];
            String type = args[1];
            String message;

            int stat = getStat(name, StatTypes.MINED.id, type);
            String material = type.replace("_", " ");
            if (stat != -1) {
                message = "§c" + name + "§f - " + WordUtils.capitalizeFully(material) + " Mined: " + df.format(stat);
            } else {
                message = "§c" + name + "§f - " + WordUtils.capitalizeFully(material) + " Mined: " + 0;
            }

            respondToCommand(message, args, sender);

            return true;
        } else {
            return false;
        }
    }

    private int getStat(String name, int type, String s) {
        Material mat = Material.matchMaterial(s);
        if (mat != null) {
            int stat;
            if (plugin.statsForPlayers.containsKey(name))
                if (plugin.statsForPlayers.get(name).containsKey(type))
                    if (plugin.statsForPlayers.get(name).get(type).containsKey(mat.toString()))
                        stat = plugin.statsForPlayers.get(name).get(type).get(mat.toString());
                    else
                        stat = 0;
                else
                    stat = 0;
            else
                stat = 0;

            return stat;
        } else {
            return -1;
        }
    }
}