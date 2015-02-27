package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Disarm extends RareItemProperty {
    private List<Material> disarmables;

    public Disarm() {
        super(
                "Disarm",
                "2% chance/level on hit to cause a target to exchange their held weapon for a random one from their inventory",
                ItemPropertyRarity.RARE,
                PropertyCostType.FOOD,
                2.0D,
                8
        );

        this.disarmables = new ArrayList() {};
        this.disarmables.add(Material.WOOD_SWORD);
        this.disarmables.add(Material.STONE_SWORD);
        this.disarmables.add(Material.IRON_SWORD);
        this.disarmables.add(Material.GOLD_SWORD);
        this.disarmables.add(Material.DIAMOND_SWORD);
        this.disarmables.add(Material.STONE_AXE);
        this.disarmables.add(Material.IRON_AXE);
        this.disarmables.add(Material.GOLD_AXE);
        this.disarmables.add(Material.DIAMOND_AXE);
        this.disarmables.add(Material.BOW);
    }

    @Override
    public boolean onDamagedOther(Player p, EntityDamageByEntityEvent e, int level) {
        if ((new Random().nextInt(100) < level * 2) &&
                ((e.getEntity() instanceof Player))) {
            Player pAttacked = (Player) e.getEntity();
            if ((pAttacked.getOpenInventory() == null) &&
                    (pAttacked.getItemInHand() != null) &&
                    (this.disarmables.contains(pAttacked.getItemInHand().getType()))) {
                int iRandomSlot = new Random().nextInt(44) + 9;

                ItemStack swapOut = pAttacked.getInventory().getItem(pAttacked.getInventory().getHeldItemSlot());
                ItemStack swapIn = pAttacked.getInventory().getItem(iRandomSlot);

                pAttacked.getInventory().setItem(pAttacked.getInventory().getHeldItemSlot(), swapIn);
                pAttacked.getInventory().setItem(iRandomSlot, swapOut);

                p.sendMessage("Disarmed " + pAttacked.getName() + "!");

                pAttacked.sendMessage("You have been disarmed!");

                return true;
            }
        }
        return false;
    }
}