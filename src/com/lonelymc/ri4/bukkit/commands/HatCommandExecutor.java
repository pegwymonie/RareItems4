package com.lonelymc.ri4.bukkit.commands;

import com.lonelymc.ri4.api.IRareItems4API;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HatCommandExecutor implements CommandExecutor{
    private final IRareItems4API api;

    public HatCommandExecutor(RareItems4Plugin plugin) {
        this.api = plugin.getAPI();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.RED + "Cannot be used from the console.");

            return true;
        }

        Player p = (Player)cs;

        ItemStack isToEquip = p.getItemInHand();

        if ((isToEquip != null) && (isToEquip.getType() != Material.AIR))
        {
            PlayerInventory inv = p.getInventory();

            ItemStack isToUnequip = inv.getHelmet();

            inv.removeItem(isToEquip);

            inv.setHelmet(isToEquip);

            inv.setItemInHand(isToUnequip);

            this.api.equipRareItem(p, isToEquip);

            this.api.unEquipRareItem(p, isToUnequip);

            p.sendMessage(ChatColor.GREEN + "Enjoy your new hat!");

            return true;
        }

        p.sendMessage(ChatColor.RED + "You can't wear that! It would look horrible and clash with your outfit.");

        return true;
    }
}
