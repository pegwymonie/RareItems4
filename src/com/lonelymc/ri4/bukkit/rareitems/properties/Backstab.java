package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Backstab extends RareItemProperty {
    public Backstab() {
        super(
                "Backstab", //Name (.toLowerCase() used as ID)
                "Deal extra damage if attacking an enemy from behind (damage * level)",// Description
                ItemPropertyRarity.RARE,
                PropertyCostType.FOOD, //Cost type
                2.0D, // Default cost
                8   // Max level
        );
    }

    @Override
    public boolean onDamagedOther(Player p, EntityDamageByEntityEvent e, int level) {
        if (e.getEntity().getLocation().getDirection().dot(e.getDamager().getLocation().getDirection()) > 0.0D) {
            // Intellij dislikes ambiguous methods or I'd use plain getDamage()
            // As it happens, getDamage() is just a wrapper for this anyway...
            e.setDamage(e.getDamage(EntityDamageEvent.DamageModifier.BASE) * level);

            p.sendMessage("Backstab!");

            if ((e.getEntity() instanceof Player)) {
                ((Player) e.getEntity()).sendMessage("You were backstabbed!");
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