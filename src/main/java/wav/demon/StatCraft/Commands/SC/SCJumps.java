package wav.demon.StatCraft.Commands.SC;

import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLQuery;
import org.bukkit.command.CommandSender;
import wav.demon.StatCraft.Commands.ResponseBuilder;
import wav.demon.StatCraft.Querydsl.QJumps;
import wav.demon.StatCraft.Querydsl.QPlayers;
import wav.demon.StatCraft.StatCraft;

import java.util.List;

public class SCJumps extends SCTemplate {

    public SCJumps(StatCraft plugin) {
        super(plugin);
        this.plugin.getBaseCommand().registerCommand("jumps", this);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission("statcraft.user.jumps");
    }

    @Override
    public String playerStatResponse(String name, List<String> args) {
        try {
            int id = plugin.getDatabaseManager().getPlayerId(name);
            if (id < 0)
                throw new Exception();

            QJumps j = QJumps.jumps;
            SQLQuery query = plugin.getDatabaseManager().getNewQuery();
            if (query == null)
                return "Sorry, there seems to be an issue connecting to the database right now.";

            Integer result = query.from(j).where(j.id.eq(id)).uniqueResult(j.amount);
            if (result == null)
                throw new Exception();

            return new ResponseBuilder(plugin)
                .setName(name)
                .setStatName("Jumps")
                .addStat("Total", df.format(result))
                .toString();
        } catch (Exception e) {
            return new ResponseBuilder(plugin)
                .setName(name)
                .setStatName("Jumps")
                .addStat("Total", String.valueOf(0))
                .toString();
        }
    }

    @Override
    public String serverStatListResponse(int num, List<String> args) {
        QJumps j = QJumps.jumps;
        QPlayers p = QPlayers.players;
        SQLQuery query = plugin.getDatabaseManager().getNewQuery();

        List<Tuple> list = query
            .from(j)
            .leftJoin(p)
            .on(j.id.eq(p.id))
            .groupBy(p.name)
            .orderBy(j.amount.desc())
            .limit(num)
            .list(p.name, j.amount);

        return topListResponse("Jumps", list);
    }
}
