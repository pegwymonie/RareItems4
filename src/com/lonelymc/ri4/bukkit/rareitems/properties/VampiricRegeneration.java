package com.lonelymc.ri4.bukkit.rareitems.properties;


import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import com.lonelymc.ri4.api.RI4Strings;
import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class VampiricRegeneration extends RareItemProperty {
    public VampiricRegeneration() {
        super(
                "Vampiric Regeneration",
                "5% chance/level to steal 1-5HP from an enemy",
                ItemPropertyRarity.RARE,
                PropertyCostType.FOOD,
                1.0D,
                5
        );
    }

    @Override
    public boolean onDamagedOther(Player pAttacker, EntityDamageByEntityEvent e, int level) {
        Random random = new Random();

        if (e.getEntity() instanceof LivingEntity
                && random.nextInt(100) < 5 * level) {

            LivingEntity attacked = (LivingEntity) e.getEntity();

            int hpToSteal = random.nextInt(4) + 1;

            double attackerNewHP = attacked.getHealth() + hpToSteal;
            double attackedNewHP = pAttacker.getHealth() - hpToSteal;

            if (attackedNewHP < 2.0D) {//1 heart
                return false;
            }

            if (attackerNewHP > pAttacker.getMaxHealth()) {
                attackerNewHP = pAttacker.getMaxHealth();
            }

            attacked.setHealth(attackedNewHP);
            pAttacker.setHealth(attackerNewHP);

            pAttacker.sendMessage(RI4Strings.RI_STOLE_HP_GAINER.replace("!hp", String.valueOf(hpToSteal)));
            pAttacker.sendMessage(RI4Strings.RI_STOLE_HP_LOSER.replace("!hp", String.valueOf(hpToSteal)));

            return true;
        }
        return false;
    }
}
