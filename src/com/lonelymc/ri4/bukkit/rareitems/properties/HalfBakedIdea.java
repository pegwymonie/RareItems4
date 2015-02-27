package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class HalfBakedIdea extends RareItemProperty {
    public HalfBakedIdea() {
        super(
                "Half-Baked Idea",
                "To the lab mouse, life is a confusing array of cheese and electricity",
                ItemPropertyRarity.STRANGE,
                PropertyCostType.FOOD,
                2.0D,
                1
        );
    }

    @Override
    public boolean onInteracted(Player pInteracted, PlayerInteractEvent e, int level){
        Random r = new Random();
        if (r.nextBoolean()) {
            for (int i = 0; i < level; i++) {
                PotionEffectType[] potionEffects = PotionEffectType.values();

                e.getPlayer().addPotionEffect(new PotionEffect(potionEffects[r.nextInt(potionEffects.length)], 1200, 1));

                e.getPlayer().sendMessage("Something happened!");
            }
            return true;
        }
        e.getPlayer().sendMessage("Nothing happened?");

        return false;
    }
}