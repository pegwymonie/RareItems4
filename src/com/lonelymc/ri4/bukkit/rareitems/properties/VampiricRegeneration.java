package com.lonelymc.ri4.bukkit.rareitems.properties;


import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class VampiricRegeneration extends RareItemProperty {
    public VampiricRegeneration() {
        super(
                "Vampiric Regeneration",
                "3% chance/level to steal 5HP from an enemy",
                ItemPropertyRarity.UNCOMMON,
                PropertyCostType.FOOD,
                1.0D,
                6
        );
    }

    @Override
    public boolean onDamagedOther(Player p, EntityDamageByEntityEvent e, int level) {
        if ((new Random().nextInt(100) < 3 * level) &&
                ((e.getEntity() instanceof LivingEntity)) && ((e.getDamager() instanceof LivingEntity))) {
            LivingEntity attacked = (LivingEntity) e.getEntity();
            LivingEntity attacker = (LivingEntity) e.getDamager();

            int iStolenHP = new Random().nextInt(3 * level) + 1;

            double iNewAttackerHP = attacked.getHealth() - iStolenHP;
            if (iNewAttackerHP > 20.0D) {
                iNewAttackerHP = 20.0D;
            }
            double iNewAttackedHP = attacker.getHealth() + iStolenHP;
            if (iNewAttackedHP < 1.0D) {
                iNewAttackerHP = 1.0D;
            }
            attacked.setHealth(iNewAttackedHP);
            attacker.setHealth(iNewAttackerHP);

            p.sendMessage(ChatColor.RED + "You stole " + iStolenHP + "HP!");
            if ((attacked instanceof Player)) {
                Player pAttacked = (Player) attacked;
                pAttacked.sendMessage(ChatColor.RED + p.getName() + " stole " + iStolenHP + "HP from you!");
            }
            return true;
        }
        return false;
    }
}
