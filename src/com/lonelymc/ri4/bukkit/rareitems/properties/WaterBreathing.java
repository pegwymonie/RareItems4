package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class WaterBreathing extends RareItemProperty {
    public WaterBreathing() {
        super(
                "Water Breathing",
                "While worn as armor or a helmet allows underwater breathing",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.PASSIVE,
                0.0D,
                1
        );
    }

    @Override
    public boolean onDamaged(Player pDamaged, EntityDamageEvent e, int level){
        if(e.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)){
            e.setCancelled(true);

            return true;
        }

        return false;
    }
}