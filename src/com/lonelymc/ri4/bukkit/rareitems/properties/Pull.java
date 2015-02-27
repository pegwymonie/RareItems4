package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Pull extends RareItemProperty {
    public Pull() {
        super(
                "Pull",
                "Pulls a clicked/hit with an arrow target towards you",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.FOOD,
                1.0D,
                1
        );
    }

    @Override
    public boolean onDamagedOther(Player pAttacker, EntityDamageByEntityEvent e, int level) {
        if ((e.getEntity() instanceof LivingEntity)) {
            if ((e.getEntity() instanceof Player)) {
                Player attacked = (Player) e.getEntity();

                attacked.sendMessage(pAttacker.getDisplayName() + ChatColor.RESET + "Pulled you towards them!");
            }
            e.getEntity().teleport(pAttacker);

            return true;
        }
        return false;
    }

    @Override
    public boolean onArrowHitEntity(Player shooter, EntityDamageByEntityEvent e, int level) {
        return onDamagedOther(shooter,e,level);
    }
}