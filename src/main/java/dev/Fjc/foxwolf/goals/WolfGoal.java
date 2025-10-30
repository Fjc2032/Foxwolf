package dev.Fjc.foxwolf.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.Fjc.foxwolf.Foxwolf;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Mob;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

// I'll just fill this with comments so it's easier to understand.

/**
 * Class that implements the Goal interface. The parameter should be the class of the mob type you want to modify.
 */
public class WolfGoal implements Goal<@NotNull Fox>, Listener {

    //Just a bunch of vars to make things easier

    /**
     * An instance of the mob. You'll want this to modify your mob later.
     */
    private final Fox mob;

    /**
     * A namespaced key, needed for the GoalKey that Paper will read.
     */
    private final NamespacedKey namespacedKey;
    /**
     * A special type of identifier that Paper uses to identify your Goal.
     */
    private static GoalKey<@NotNull Fox> goalKey;

    private final World world;

    private boolean isAttackedByWolf;


    public WolfGoal(@NotNull Foxwolf plugin, Mob entity) {
        this.mob = (entity instanceof Fox fox) ? fox : null;
        this.world = mob.getWorld();

        this.namespacedKey = new NamespacedKey(plugin, "wolfgoal");
        goalKey = GoalKey.of(Fox.class, namespacedKey);
    }

    /**
     * What conditions should be met for this goal to activate? Set a boolean condition or set to true for all times.
     * @return Whether this goal should activate.
     */
    @Override
    public boolean shouldActivate() {
        return !getNearbyWolves().isEmpty() || isAttackedByWolf;
    }

    /**
     * What conditions determine whether the goal remains active after it's already activated?
     * @return Whether this goal should stay active.
     */
    @Override
    public boolean shouldStayActive() {
        return (!getNearbyWolves().isEmpty() && mob.getTarget() != null) || isAttackedByWolf;
    }

    /**
     * What should happen when the goal starts?
     */
    @Override
    public void start() {
        Wolf target = getNearestWolf();
        if (target != null) {
            mob.setTarget(target);
            mob.setAggressive(true);
        }
    }

    /**
     * What should happen when the goal stops?
     * @apiNote The goal won't stop by itself. You have to call the proper method to stop the goal elsewhere.
     */
    @Override
    public void stop() {
        mob.setTarget(null);
        mob.setAggressive(false);
        isAttackedByWolf = false;
    }

    /**
     * What should happen while the goal is active? This gets called for every tick the goal is active.
     */
    @Override
    public void tick() {
        if (mob.getTarget() == null || mob.getTarget().isDead()) {
            Wolf target = getNearestWolf();
            if (target != null) {
                mob.setTarget(target);
                mob.setAggressive(true);
            }
        }
    }

    @Override
    public @NotNull GoalKey<@NotNull Fox> getKey() {
        return goalKey;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET, GoalType.MOVE);
    }

    private List<Wolf> getNearbyWolves() {
        return world.getNearbyEntitiesByType(Wolf.class, mob.getLocation(), 50).stream().toList();
    }

    private Wolf getNearestWolf() {
        return getNearbyWolves().stream()
                .filter(wolf -> !wolf.isTamed())
                .min((obj1, obj2) -> Double.compare(
                        mob.getLocation().distanceSquared(obj1.getLocation()),
                        mob.getLocation().distanceSquared(obj2.getLocation())
                )).orElse(null);
    }

    private boolean isInRange(Location target) {
        Location foxPos = mob.getLocation();
        return foxPos.distance(target) <= 24;
    }

    public @Nullable World getWorld() {
        return this.world;
    }

    public @NotNull NamespacedKey getNamespacedKey() {
        return this.namespacedKey;
    }

    public Mob getMob() {
        return (this.mob instanceof Fox fox) ? fox : null;
    }

    @EventHandler
    public void onWolfAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() != mob) return;
        if (event.getDamager() instanceof Wolf) {
            this.isAttackedByWolf = true;
            return;
        }
        if (event.getDamager() instanceof PolarBear) {
            this.isAttackedByWolf = true;
            return;
        }
        this.isAttackedByWolf = false;
    }
}

// world.getTime() >= 13000L
