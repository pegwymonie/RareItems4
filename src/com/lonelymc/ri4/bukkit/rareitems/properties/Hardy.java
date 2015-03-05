package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
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
                3,
                new String[]{
                        "type=DIAMOND_BLOCK;amount=1;",
                        "type=ANVIL;amount=1;",
                        "type=DIAMOND_BLOCK;amount=1;",
                        "type=ANVIL;amount=1;",
                        "!LEGENDARY_ESSENCE",
                        "type=ANVIL;amount=1;",
                        "type=DIAMOND_BLOCK;amount=1;",
                        "type=NETHER_STAR;amount=1;",
                        "type=DIAMOND_BLOCK;amount=1;"
                }
        );
    }

    @Override
    public boolean onDamaged(Player pDamaged, EntityDamageEvent e, int level) {
        e.setDamage(e.getDamage() - level);

        return true;
    }
}