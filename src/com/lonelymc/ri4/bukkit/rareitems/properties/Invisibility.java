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

public class Invisibility extends RareItemProperty {
    public Invisibility() {
        super(
                "Invisibility",
                "Invisibility for 60 seconds per level",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.EXPERIENCE,
                1.0D,
                20
        );
    }

    @Override
    public boolean onInteracted(Player pInteracted, PlayerInteractEvent e, int level) {
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200 * level, 1));

        e.getPlayer().sendMessage("You are now invisible!");

        return true;
    }

    @Override
    public boolean onInteractEntity(Player pInteracted, PlayerInteractEntityEvent e, int level){
        if ((e.getRightClicked() instanceof LivingEntity)) {
            LivingEntity le = (LivingEntity) e.getRightClicked();

            le.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200 * level, 1));
            if ((le instanceof Player)) {
                e.getPlayer().sendMessage("You cast " + getName() + " on " + ((Player) le).getName() + "!");
                ((Player) le).sendMessage(e.getPlayer().getName() + " cast " + getName() + " on you!");
            } else {
                e.getPlayer().sendMessage("You cast " + getName() + " on that thing!");
            }
            return true;
        }
        return false;
    }
}