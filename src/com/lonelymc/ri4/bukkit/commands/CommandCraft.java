package com.lonelymc.ri4.bukkit.commands;

import com.lonelymc.ri4.api.IRareItem;
import com.lonelymc.ri4.api.IRareItemProperty;
import com.lonelymc.ri4.api.IRareItems4API;
import com.lonelymc.ri4.api.RI4Strings;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class CommandCraft extends BasicCommand {
    private final IRareItems4API api;

    public CommandCraft(IRareItems4API api) {
        super(
            RI4Strings.COM_CRAFT, 
            RI4Strings.COM_CRAFT_USAGE,
            RI4Strings.COM_CRAFT_DESC,
            "rih.admin.craft"
        );
        
        this.api = api;
    }	
    
    @Override
    boolean execute(CommandSender cs, String[] args){
        if(args.length < 1){
            this.send(cs,this.getUsage());

            return true;
        }

        if(!(cs instanceof Player)){
            this.sendError(cs,RI4Strings.COMMAND_NOT_FROM_CONSOLE);

            return true;
        }

        Player player = (Player) cs;

        String propertyName = args[0].replace("_"," ");

        IRareItemProperty property = this.api.getItemPropertyByDisplayName(propertyName);

        if(property == null){
            this.sendError(cs,RI4Strings.COMMAND_VALUE_NOT_FOUND.replace("!value",propertyName));

            return true;
        }

        int level = 1;

        if(args.length > 1){
            String sLevel = args[1];

            try{
                level = Integer.parseInt(sLevel);
            }
            catch(NumberFormatException ex){
                this.sendError(cs, RI4Strings.COMMAND_INVALID_LEVEL.replace("!value", sLevel));

                return true;
            }
        }

        ItemStack isInHand = player.getItemInHand();

        if(isInHand == null || isInHand.getType().equals(Material.AIR)){
            this.sendError(cs,RI4Strings.COMMAND_MUST_HOLD_ITEM);

            return true;
        }

        IRareItem rareItem = this.api.getRareItem(isInHand);

        Map<IRareItemProperty,Integer> properties = new HashMap<>();

        properties.put(property,1);

        if(rareItem == null){
            rareItem = this.api.createRareItem(player.getUniqueId(),properties);
        }
        else{
            properties.putAll(rareItem.getProperties());

            this.api.setRareItemProperties(player.getUniqueId(), rareItem.getId(), properties);
        }

        ItemMeta meta = isInHand.getItemMeta();

        meta.setLore(RI4Strings.getLore(rareItem));

        isInHand.setItemMeta(meta);

        this.send(cs,RI4Strings.COMMAND_ADDED_PROPERTY_TO_ITEM
                .replace("!property", property.getDisplayName()));

        return true;
    }
}
