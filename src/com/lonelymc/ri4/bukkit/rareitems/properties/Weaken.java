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

public class Weaken extends RareItemProperty {
    public Weaken() {
        super(
                "Weaken",
                "25% chance to weaken an enemy for 3 seconds/level",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.FOOD,
                1.0D,
                4
        );
    }

    @Override
    public boolean onDamagedOther(Player p, EntityDamageByEntityEvent e, int level) {
        if ((new Random().nextInt(4) == 0) &&
                ((e.getEntity() instanceof LivingEntity))) {
            LivingEntity le = (LivingEntity) e.getEntity();

            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60 * level, 1));

            p.sendMessage("Weakened!");
            if ((e.getEntity() instanceof Player)) {
                ((Player) e.getEntity()).sendMessage("You are weakened!");
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