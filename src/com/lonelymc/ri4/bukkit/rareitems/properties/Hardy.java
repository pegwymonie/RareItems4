package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class Hardy extends RareItemProperty {
    public Hardy() {
        super(
                "Hardy",
                "While worn as armor or a helmet decreases damage by -1 per lvl",
                ItemPropertyRarity.LEGENDARY,
                PropertyCostType.PASSIVE,
                0.0D,
                3
        );
    }

    @Override
    public boolean onDamaged(Player pDamaged, EntityDamageEvent e, int level) {
        e.setDamage(e.getDamage()-level);

        return true;
    }
}