package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CatsFeet extends RareItemProperty {
    public CatsFeet() {
        super(
                "Cat's Feet",
                "Lets you or a clicked target jump higher for 60 seconds per level",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.EXPERIENCE,
                1.0D,
                30,
                new String[]{
                        "type=POTION;dura=8258;amount=1;",
                        "type=FEATHER;amount=1;",
                        "type=POTION;dura=8258;amount=1;",
                        "type=GLOWSTONE;amount=1;",
                        "ESSENCE_ITEM",
                        "type=GLOWSTONE;amount=1;",
                        "type=POTION;dura=8258;amount=1;",
                        "type=FEATHER;amount=1;",
                        "type=POTION;dura=8258;amount=1;"
                }
        );
    }

    @Override
    public boolean onInteracted(Player pInteracted, PlayerInteractEvent e, int level) {
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1200 * level, level));

        e.getPlayer().sendMessage("You can jump higher!");

        return true;
    }

    @Override
    public boolean onInteractEntity(Player pInteracted, PlayerInteractEntityEvent e, int level) {
        if ((e.getRightClicked() instanceof LivingEntity)) {
            int duration = 1200 * level;

            LivingEntity le = (LivingEntity) e.getRightClicked();

            le.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, level));
            if ((le instanceof Player)) {
                e.getPlayer().sendMessage("You cast Cat's Feet on " + ((Player) le).getName() + "!");
                ((Player) le).sendMessage(e.getPlayer().getName() + " cast Cat's Feet on you!");
            } else {
                e.getPlayer().sendMessage("You cast Cat's Feet on that thing!");
            }
            return true;
        }
        return false;
    }
}