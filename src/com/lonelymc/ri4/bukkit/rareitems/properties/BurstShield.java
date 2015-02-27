package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import com.lonelymc.ri4.util.FireworkVisualEffect;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BurstShield extends RareItemProperty {
    private final FireworkVisualEffect fireworks;

    public BurstShield() {
        super(
                "Burst Shield",
                "While worn on adds 10% chance per level to send an enemy flying away",
                ItemPropertyRarity.LEGENDARY,
                PropertyCostType.FOOD,
                2, //cost
                3  //max level
        );

        this.fireworks = new FireworkVisualEffect();
    }

    @Override
    public boolean onDamagedOther(Player pAttacker, EntityDamageByEntityEvent e, int level){
        if(new Random().nextInt(100) > level * 10){
            return false;
        }

        try {
            this.fireworks.playFirework(pAttacker
                            .getWorld(), pAttacker.getLocation(),

                    FireworkEffect.builder()
                            .with(FireworkEffect.Type.BURST)
                            .withColor(Color.WHITE)
                            .build());
        } catch (Exception ex) {
            Logger.getLogger(BurstShield.class.getName()).log(Level.SEVERE, null, ex);
        }

        Vector unitVector = e.getEntity().getLocation().toVector().subtract(e.getDamager().getLocation().toVector()).normalize();

        unitVector.setY(0.55D / level);

        pAttacker.setVelocity(unitVector.multiply(level * 2));

        e.setCancelled(true);

        pAttacker.sendMessage("You were knocked back by burst shield!");

        ((Player) e.getEntity()).sendMessage("Burst shield activated!");

        return true;
    }
}