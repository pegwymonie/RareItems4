package com.lonelymc.ri4.bukkit.commands;

import com.lonelymc.ri4.api.*;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import com.lonelymc.ri4.bukkit.rareitems.Essence;
import com.lonelymc.ri4.util.FakeInventory;
import com.lonelymc.ri4.util.ItemStackConvertor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CommandWI extends BasicCommand {
    private final IRareItems4API api;
    private final RareItems4Plugin plugin;

    public CommandWI(RareItems4Plugin plugin) {
        super(
                RI4Strings.COM_WI,
                RI4Strings.COM_WI_USAGE,
                RI4Strings.COM_WI_DESC,
                "ri4.wi"
        );

        this.plugin = plugin;
        this.api = plugin.getAPI();
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

            TextComponent witc = new TextComponent("");

            for (IRareItemProperty rip : allProperties) {
                switch (rip.getRarity()) {
                    default: //case COMMON:
                        witc.addExtra(this.getMultilineDescription(rip, RI4Strings.RAREITEM_COMMON));
                        break;
                    case UNCOMMON:
                        witc.addExtra(this.getMultilineDescription(rip, RI4Strings.RAREITEM_UNCOMMON));
                        break;
                    case RARE:
                        witc.addExtra(this.getMultilineDescription(rip, RI4Strings.RAREITEM_RARE));
                        break;
                    case LEGENDARY:
                        witc.addExtra(this.getMultilineDescription(rip, RI4Strings.RAREITEM_LEGENDARY));
                        break;
                    case STRANGE:
                        witc.addExtra(this.getMultilineDescription(rip, RI4Strings.RAREITEM_STRANGE));
                        break;
                }
            }

            if (cs instanceof Player) {
                Player player = (Player) cs;
                player.spigot().sendMessage(witc);
            } else {
                cs.sendMessage(witc.toLegacyText());
            }
        } else {//args.length > 0
            String sPropertyName = args[0].replace("_"," ");

            IRareItemProperty rip = this.api.getItemProperty(sPropertyName);

            if (rip == null) {
                this.sendError(cs, RI4Strings.COMMAND_INVALID_PROPERTY
                        .replace("!property", sPropertyName));

                return true;
            }

            String[] ripRecipe = rip.getRecipe();

            if(ripRecipe == null){
                this.sendError(cs,RI4Strings.COMMAND_NO_RIP_RECIPE_EXISTS.replace("!property",rip.getDisplayName()));

                return true;
            }

            Player p = (Player) cs;

            Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.WORKBENCH, RI4Strings.CRAFTING_VIEW_RARE_ITEM_RECIPE);

            for(int i=0;i<ripRecipe.length;i++){
                String sIngredient = ripRecipe[i];

                switch (sIngredient) {
                    default:
                        ItemStack is = ItemStackConvertor.fromString(sIngredient);

                        inv.setItem(i+1,is);

                        break;
                    case "AIR":
                        break;
                    case "!COMMON_ESSENCE":
                    case "!UNCOMMON_ESSENCE":
                    case "!RARE_ESSENCE":
                    case "!LEGENDARY_ESSENCE":
                    case "!STRANGE_ESSENCE":
                        String sRarity = sIngredient.substring(1, sIngredient.indexOf("_"));

                        ItemPropertyRarity rarity = ItemPropertyRarity.valueOf(sRarity);

                        IEssence dummyEssence = this.api.generateDummyEssence(rarity);

                        ItemStack isEssence = new ItemStack(Material.valueOf(dummyEssence.getMaterial()));

                        ItemMeta meta = isEssence.getItemMeta();

                        meta.setDisplayName(RI4Strings.getDisplayName(dummyEssence));

                        meta.setLore(RI4Strings.getItemLore(dummyEssence));

                        isEssence.setItemMeta(meta);

                        inv.setItem(i+1,isEssence);

                        break;
                }
            }

            p.openInventory(inv);

            ItemStack isResult = new ItemStack(Material.valueOf(Essence.getMaterialByRarity(rip.getRarity())));

            ItemMeta meta = isResult.getItemMeta();

            meta.setDisplayName(RI4Strings.getRareItemLoreString(rip,0));

            String[] loreLines = RI4Strings.COMMAND_MULTILINE_RI_DESCRIPTION
                    .replace("!property", rip.getDisplayName())
                    .replace("!rarity", String.valueOf(rip.getRarity()))
                    .replace("!costMsg", RI4Strings.getCostMessage(rip.getCost(), rip.getCostType()))
                    .replace("!maxLevel", String.valueOf(rip.getMaxLevel()))
                    .replace("!description", WordUtils.wrap(rip.getDescription(), 40, "\n", true))
                    .split("\n");

            meta.setLore(new ArrayList<String>(Arrays.asList(loreLines)));

            isResult.setItemMeta(meta);

            FakeInventory.fakeClientInventorySlot(this.plugin, inv.getViewers(), isResult, 0);

            inv.setItem(0,isResult);
        }

        return true;

    }

    private TextComponent getMultilineDescription(IRareItemProperty rip, String rarityColor) {
        TextComponent text = new TextComponent(TextComponent.fromLegacyText(rarityColor
                .replace("!property", rip.getDisplayName())
                .replace("!level", "") + ChatColor.GRAY + "| "));

        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(RI4Strings.COMMAND_MULTILINE_RI_DESCRIPTION
                .replace("!property", rip.getDisplayName())
                .replace("!rarity", String.valueOf(rip.getRarity()))
                .replace("!costMsg", RI4Strings.getCostMessage(rip.getCost(), rip.getCostType()))
                .replace("!maxLevel", String.valueOf(rip.getMaxLevel()))
                .replace("!description", org.apache.commons.lang.WordUtils.wrap(rip.getDescription(), 40, "\n", true))
        ).create()));

        return text;
    }
}

class RIDisplayNameComparator implements Comparator<IRareItemProperty> {
    @Override
    public int compare(IRareItemProperty rip1, IRareItemProperty rip2) {
        return rip1.getDisplayName().compareTo(rip2.getDisplayName());
    }
}