package com.lonelymc.ri4.bukkit.rareitems.properties;

import com.lonelymc.ri4.api.ItemPropertyRarity;
import com.lonelymc.ri4.api.PropertyCostType;
import com.lonelymc.ri4.bukkit.rareitems.RareItemProperty;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RepairItem extends RareItemProperty {
    public RepairItem() {
        super(
                "Repair Item",
                "Repairs the #1 hotbar slot item",
                ItemPropertyRarity.RARE,
                PropertyCostType.EXPERIENCE,
                1.0D,
                100,
                new String[]{
                        "type=POTION;dura=8225;amount=1;",
                        "type=ANVIL;amount=1;",
                        "type=POTION;dura=8225;amount=1;",
                        "type=ANVIL;amount=1;",
                        "!RARE_ESSENCE",
                        "type=ANVIL;amount=1;",
                        "type=POTION;dura=8225;amount=1;",
                        "type=IRON_BLOCK;amount=1;",
                        "type=POTION;dura=8225;amount=1;"
                }
        );
    }

    @Override
    public boolean onInteracted(Player pInteracted, PlayerInteractEvent e, int level) {
        ItemStack isSlotOne = e.getPlayer().getInventory().getItem(0);
        if ((isSlotOne != null) && (isSlotOne.getType().getMaxDurability() > 20) && (isSlotOne.getDurability() > 0)) {
            int iRepairAmount = isSlotOne.getType().getMaxDurability() / 5 * level - isSlotOne.getType().getMaxDurability() / 10;

            short sDurability = (short) (isSlotOne.getDurability() - iRepairAmount);
            if (sDurability < 0) {
                sDurability = 0;
            }
            isSlotOne.setDurability(sDurability);

            e.getPlayer().sendMessage("Item repaired by " + level * 20 + "%!");

            return true;
        }
        e.getPlayer().sendMessage("Item in slot #1 is not repairable!");


        return false;
    }

    @Override
    public boolean onInteractEntity(Player pInteracted, PlayerInteractEntityEvent e, int level) {
        if ((e.getRightClicked() instanceof Player)) {
            Player pClicked = (Player) e.getRightClicked();
            ItemStack isSlotOne = pClicked.getItemInHand();
            if (isSlotOne.getType().getMaxDurability() > 20) {
                int iRepairAmount = isSlotOne.getType().getMaxDurability() / 5 * level - isSlotOne.getType().getMaxDurability() / 10;

                short sDurability = (short) (isSlotOne.getDurability() - iRepairAmount);
                if (sDurability < 0) {
                    sDurability = 0;
                }
                isSlotOne.setDurability(sDurability);

                e.getPlayer().sendMessage("Item in " + pClicked.getName() + "'s hand repaired by " + level * 20 + "%!");

                return true;
            }
            e.getPlayer().sendMessage("Item in " + pClicked.getName() + "'s hand is not repairable!");


            return true;
        }
        return false;
    }
}