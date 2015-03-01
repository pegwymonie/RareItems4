package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Spore extends RareItemProperty {
    public Spore() {
        super(
                "Spore",
                "Turns clicked cobblestone into mossy cobblestone.",
                ItemPropertyRarity.COMMON,
                PropertyCostType.COOLDOWN,
                1.0D,
                1,
                new String[]{
                        "type=SEEDS;amount=1;",
                        "type=COBBLESTONE;amount=1;",
                        "type=SEEDS;amount=1;",
                        "type=COBBLESTONE;amount=1;",
                        "!COMMON_ESSENCE",
                        "type=COBBLESTONE;amount=1;",
                        "type=SEEDS;amount=1;",
                        "type=COBBLESTONE;amount=1;",
                        "type=SEEDS;amount=1;"
                }
        );
    }

    @Override
    public boolean onInteracted(Player pInteracted, PlayerInteractEvent e, int level) {
        if ((e.getClickedBlock() != null) && (e.getClickedBlock().getType() == Material.COBBLESTONE)) {
            e.getClickedBlock().setType(Material.MOSSY_COBBLESTONE);

            return true;
        }
        return false;
    }
}