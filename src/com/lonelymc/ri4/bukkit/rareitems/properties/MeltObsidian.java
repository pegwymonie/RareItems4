package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class MeltObsidian extends RareItemProperty {
    public MeltObsidian() {
        super(
                "Melt Obsidian",
                "Turns clicked lava into obsidian",
                ItemPropertyRarity.COMMON,
                PropertyCostType.COOLDOWN,
                1.0D,
                1,
                new String[]{
                        "type=OBSIDIAN;amount=1;",
                        "type=LAVA_BUCKET;amount=1;",
                        "type=OBSIDIAN;amount=1;",
                        "type=LAVA_BUCKET;amount=1;",
                        "ESSENCE_ITEM",
                        "type=LAVA_BUCKET;amount=1;",
                        "type=OBSIDIAN;amount=1;",
                        "type=LAVA_BUCKET;amount=1;",
                        "type=OBSIDIAN;amount=1;"
                }
        );
    }

    @Override
    public boolean onInteracted(Player pInteracted, PlayerInteractEvent e, int level) {
        if (e.getClickedBlock() != null) {
            if (e.getClickedBlock().getType() == Material.OBSIDIAN) {
                e.getClickedBlock().setType(Material.LAVA);

                e.setCancelled(true);

                return true;
            }
        }
        return false;
    }
}