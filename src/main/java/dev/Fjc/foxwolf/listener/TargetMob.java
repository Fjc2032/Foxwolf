package dev.Fjc.foxwolf.listener;

import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import dev.Fjc.foxwolf.Foxwolf;
import dev.Fjc.foxwolf.goals.WolfGoal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Fox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TargetMob implements Listener {

    private final Foxwolf plugin;

    private final MobGoals mobGoals = Bukkit.getMobGoals();

    public TargetMob(@NotNull Foxwolf plugin) {
        this.plugin = plugin;
    }

    /**
     * You don't have to use an event, but you do need to call Bukkit.getMobGoals() to add the goal to your mob.
     * @param event In this case, the event, which is given.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Fox fox : Arrays.stream(event.getChunk().getEntities())
                .filter(filter -> filter instanceof Fox)
                .map(mapper -> (Fox) mapper)
                .toList()) {
            WolfGoal wolfGoal = new WolfGoal(this.plugin, fox);
            if (this.mobGoals.hasGoal(fox, wolfGoal.getKey())) continue;
            this.mobGoals.addGoal(fox, 0, wolfGoal);
        }
    }

    /**
     * You don't have to use an event, but you do need to call Bukkit.getMobGoals() to add the goal to your mob.
     * @param event In this case, the event, which is given.
     */
    @EventHandler
    public void onEntityAdd(EntityAddToWorldEvent event) {
        if (!(event.getEntity() instanceof Fox fox)) return;
        WolfGoal wolfGoal = new WolfGoal(this.plugin, fox);
        if (this.mobGoals.hasGoal(fox, wolfGoal.getKey())) return;
        Bukkit.getMobGoals().addGoal(fox, 0, wolfGoal);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Fox fox)) return;
        double modifier = fox.getTicksLived();
        event.setDamage(event.getDamage() * (1 + (modifier / 100)));
    }
}
