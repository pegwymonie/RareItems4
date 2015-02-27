package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Blinding extends RareItemProperty {
    public Blinding() {
        super(
                "Blinding",
                "25% chance to blind a target onhit (3s / level)",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.FOOD,
                2.0D,
                5
        );
    }

    @Override
    public boolean onDamaged(Player pDamaged, EntityDamageEvent e, int level) {
        if ((new Random().nextInt(4) == 0) &&
                ((e.getEntity() instanceof LivingEntity))) {
            LivingEntity le = (LivingEntity) e.getEntity();

            le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * level, 2));

            pDamaged.sendMessage("Blinded!");
            if ((e.getEntity() instanceof Player)) {
                ((Player) e.getEntity()).sendMessage("You are blinded!");
            }
            return true;
        }
        return false;
    }
}