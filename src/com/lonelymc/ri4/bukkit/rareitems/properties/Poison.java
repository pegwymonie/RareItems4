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

public class Poison extends RareItemProperty {
    public Poison() {
        super(
                "Poison",
                "3% chance/level to poison an enemy onhit",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.FOOD,
                2.0D,
                6
        );
    }

    @Override
    public boolean onDamagedOther(Player attacker, EntityDamageByEntityEvent e, int level) {
        if ((new Random().nextInt(100) < 3 * level) &&
                ((e.getEntity() instanceof LivingEntity))) {
            LivingEntity le = (LivingEntity) e.getEntity();

            le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 180, 1 * level));

            attacker.sendMessage("Poisoned!");
            if ((e.getEntity() instanceof Player)) {
                ((Player) e.getEntity()).sendMessage("You are poisoned!");
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