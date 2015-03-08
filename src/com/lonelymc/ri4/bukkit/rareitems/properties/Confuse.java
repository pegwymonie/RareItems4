package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Confuse extends RareItemProperty {
    public Confuse() {
        super(
                "Confuse",
                "25% chance on hit to severely confuse a target for 5 seconds/level",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.FOOD,
                1,
                4
        );
    }

    @Override
    public boolean onDamagedOther(Player p, EntityDamageByEntityEvent e, int level) {
        if ((new Random().nextInt(4) == 0) &&
                ((e.getEntity() instanceof LivingEntity))) {
            LivingEntity le = (LivingEntity) e.getEntity();

            le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5 * level, level));

            p.sendMessage("Confused!");
            if ((e.getEntity() instanceof Player)) {
                ((Player) e.getEntity()).sendMessage("You are confused!");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onArrowHitEntity(Player shooter, EntityDamageByEntityEvent e, int level) {
        return onDamagedOther(shooter,e,level);
    }
}