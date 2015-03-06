package com.lonelymc.ri4.bukkit.commands;

import com.lonelymc.ri4.api.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandEssence extends BasicCommand {
    private final IRareItems4API api;
    
    public CommandEssence(IRareItems4API api) {
        super(
            RI4Strings.COM_ESSENCE,
            RI4Strings.COM_ESSENCE_USAGE,
            RI4Strings.COM_ESSENCE_DESC,
            "rih.admin.essence"
        );
        
        this.api = api;
    }	
    
    @Override
    boolean execute(CommandSender cs, String[] args){
        if(args.length < 1){
            this.send(cs,this.getUsage());

            String sTypes = "";
            for(ItemPropertyRarity rarity : ItemPropertyRarity.values()){
                sTypes += ", "+rarity.name();
            }
            sTypes = sTypes.substring(2);

            cs.sendMessage("Valid types are: "+sTypes);

            return true;
        }

        Player player;

        if(args.length > 1){
            String playerName = args[1];

            player = Bukkit.getPlayer(playerName);

            if(player == null){
                this.sendError(cs,RI4Strings.COMMAND_PLAYER_NOT_FOUND.replace("!player",playerName));

                return true;
            }
        }
        else if(cs instanceof Player){
            player = (Player) cs;
        }
        else {
            this.sendError(cs,"If used from console you must specify a player!");

            return true;
        }

        Player creator;

        if(cs instanceof Player){
            creator = (Player) cs;
        }
        else {
            creator = player;
        }

        String essenceType = args[0].replace("_"," ");

        IEssence essence;

        // blank essences
        try{
            ItemPropertyRarity rarity = ItemPropertyRarity.valueOf(essenceType.toUpperCase());

            essence = this.api.createEssence(creator.getUniqueId(), rarity);
        }
        catch (IllegalArgumentException e) {
            // essence with an item property
            IRareItemProperty rip = this.api.getItemPropertyByDisplayName(essenceType);

            if(rip != null){
                essence = this.api.createEssence(creator.getUniqueId(), rip);
            }
            else {
                this.sendError(cs,essenceType + " is not a valid essence or item property type!");

                return true;
            }
        }

        ItemStack isEssence = new ItemStack(Material.valueOf(essence.getMaterial()));

        ItemMeta meta = isEssence.getItemMeta();

        meta.setDisplayName(RI4Strings.getDisplayName(essence));

        meta.setLore(RI4Strings.getItemLore(essence));

        isEssence.setItemMeta(meta);

        if (!player.getInventory().addItem(new ItemStack[] { isEssence }).isEmpty()) {
            this.sendError(player,"Tried to give you a "+ RI4Strings.getDisplayName(essence)+ ChatColor.RED+" essence but your inventory was full, so it was dropped on the ground");

            player.getWorld().dropItemNaturally(player.getLocation(), isEssence);
        }

        if(player == cs){
            this.send(cs,"You got "+ RI4Strings.getDisplayName(essence)+ChatColor.RESET+"!");
        }
        else {
            this.send(cs,"Gave "+player.getName()+" "+ RI4Strings.getDisplayName(essence)+ChatColor.RESET+"!");

            this.send(player,"You got "+ RI4Strings.getDisplayName(essence)+ChatColor.RESET+"!");
        }

        return true;
    }
}
