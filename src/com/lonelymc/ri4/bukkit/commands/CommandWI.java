package com.lonelymc.ri4.bukkit.commands;

import com.lonelymc.ri4.api.IRareItemProperty;
import com.lonelymc.ri4.api.IRareItems4API;
import com.lonelymc.ri4.api.RI4Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommandWI extends BasicCommand {
    private final IRareItems4API api;

    public CommandWI(IRareItems4API api) {
        super(
                RI4Strings.COM_WI,
                RI4Strings.COM_WI_USAGE,
                RI4Strings.COM_WI_DESC,
                "ri4.wi"
        );

        this.api = api;
    }

    @Override
    boolean execute(CommandSender cs, String[] args) {
        if (args.length > 0 && !(cs instanceof Player)) {
            this.sendError(cs, RI4Strings.COMMAND_NOT_FROM_CONSOLE);

            return true;
        }

        if (args.length == 0) {
            StringBuilder sb = new StringBuilder();

            List<IRareItemProperty> allProperties = new ArrayList(this.api.getAllItemProperties());

            //Alphabetize by display name
            Collections.sort(allProperties, new RIDisplayNameComparator());

            for (IRareItemProperty rip : allProperties) {
                switch (rip.getRarity()) {
                    default: //case COMMON:
                        sb.append(ChatColor.GRAY+"| " + RI4Strings.RAREITEM_COMMON
                                .replace("!property", rip.getDisplayName())
                                .replace("!level", ""));
                        break;
                    case UNCOMMON:
                        sb.append(ChatColor.GRAY+"| " + RI4Strings.RAREITEM_UNCOMMON
                                .replace("!property", rip.getDisplayName())
                                .replace("!level", ""));
                        break;
                    case RARE:
                        sb.append(ChatColor.GRAY+"| " + RI4Strings.RAREITEM_RARE
                                .replace("!property", rip.getDisplayName())
                                .replace("!level", ""));
                        break;
                    case LEGENDARY:
                        sb.append(ChatColor.GRAY+"| " + RI4Strings.RAREITEM_LEGENDARY
                                .replace("!property", rip.getDisplayName())
                                .replace("!level", ""));
                        break;
                    case STRANGE:
                        sb.append(ChatColor.GRAY+"| " + RI4Strings.RAREITEM_STRANGE
                                .replace("!property", rip.getDisplayName())
                                .replace("!level", ""));
                        break;
                }
            }

            if (sb.length() > 1) {
                sb.delete(0, 2);
            }

            this.send(cs, RI4Strings.COMMAND_AVAILABLE_PROPERTIES, sb.toString());

            return true;
        }

        String sPropertyName = args[0].replace("_", " ");

        IRareItemProperty rip = this.api.getItemProperty(sPropertyName);

        if (rip == null) {
            this.sendError(cs, RI4Strings.COMMAND_INVALID_PROPERTY
                    .replace("!property", sPropertyName));

            return true;
        }

        Player p = (Player) cs;

        Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.WORKBENCH, RI4Strings.CRAFTING_RARE_ITEM_RECIPE);

        p.openInventory(inv);

        return true;
    }
}

class RIDisplayNameComparator implements Comparator<IRareItemProperty> {
    @Override
    public int compare(IRareItemProperty rip1, IRareItemProperty rip2) {
        return rip1.getDisplayName().compareTo(rip2.getDisplayName());
    }
}