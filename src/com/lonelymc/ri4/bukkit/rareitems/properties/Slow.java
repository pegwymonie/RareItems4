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

public class Slow extends RareItemProperty {
    public Slow() {
        super(
                "Slow",
                "3% chance/level to slow an attacked enemy",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.FOOD,
                1.0D,
                8
        );
    }

    public boolean onDamageOther(EntityDamageByEntityEvent e, Player p, int level) {
        if ((new Random().nextInt(100) < level * 3) &&
                ((e.getEntity() instanceof LivingEntity))) {
            LivingEntity le = (LivingEntity) e.getEntity();

            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, level));

            p.sendMessage("Slowed!");
            if ((e.getEntity() instanceof Player)) {
                ((Player) e.getEntity()).sendMessage("You are slowed!");
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