package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Strength extends RareItemProperty {
    public Strength() {
        super(
                "Strength",
                "While worn as armor or a helmet adds 1 damage per lvl",
                ItemPropertyRarity.LEGENDARY,
                PropertyCostType.PASSIVE,
                0.0D,
                3
        );
    }

    @Override
    public boolean onDamagedOther(Player pAttacker, EntityDamageByEntityEvent e, int level) {
        e.setDamage(e.getDamage()+level);

        return true;
    }
}