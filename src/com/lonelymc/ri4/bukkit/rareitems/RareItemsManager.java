package com.lonelymc.ri4.bukkit.rareitems;

import com.lonelymc.ri4.api.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.*;

public class RareItemsManager implements IRareItems4API {
    private final Map<String, IRareItemProperty> properties;
    private final Map<String, IRareItemProperty> propertiesLookup;
    private final HashMap<UUID, Map<IRareItemProperty, Integer>> activeEffects;
    private final IRareItemsPersistence persistence;

    public RareItemsManager(JavaPlugin plugin, IRareItemsPersistence persistence) {
        this.propertiesLookup = new HashMap<>();
        this.properties = new HashMap<>();
        this.activeEffects = new HashMap<>();

        this.persistence = persistence;
        this.persistence.setAPI(this);

        // Garbage collection
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                // Clear out any cooldowns that have passed
                for (IRareItemProperty rip : propertiesLookup.values()) {
                    rip.refreshCooldowns();
                }
            }
        }, 20 * 60 * 10, 20 * 60 * 10);

        IRareItems4API theApi = this;

        // Active effects cleaner
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            private int runCount = 0;
            private final IRareItems4API api = theApi;

            public void run() {
                // 600/20 = ~every 30 seconds
                if (this.runCount > 600) {
                    this.runCount = 1;

                    Iterator<Map.Entry<UUID, Map<IRareItemProperty, Integer>>> iterator = activeEffects.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Map.Entry<UUID, Map<IRareItemProperty, Integer>> next = iterator.next();

                        Player p = Bukkit.getServer().getPlayer(next.getKey());

                        if (p == null) {
                            iterator.remove();
                        } else {
                            // refresh rare items the player is wearing - done to protect against glitches
                            next.getValue().clear();

                            for (ItemStack is : p.getInventory().getArmorContents()) {
                                if ((is != null) && (is.getType() != Material.AIR)) {
                                    api.equipRareItem(p, is);
                                }
                            }
                        }
                    }
                } else {
                    this.runCount += 1;
                }

                for (Map.Entry<UUID, Map<IRareItemProperty, Integer>> playerActiveEffects : activeEffects.entrySet()) {
                    Player p = Bukkit.getServer().getPlayer(playerActiveEffects.getKey());

                    for (Map.Entry<IRareItemProperty, Integer> e : playerActiveEffects.getValue().entrySet()) {
                        e.getKey().applyEffectToPlayer(p, e.getValue());
                    }
                }
            }
        }, 20L, 20L);

        this.customizationsYml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "rareItemCustomizations.yml"));
    }

    @Override
    public void addItemProperty(IRareItemProperty property) {
        String displayName = property.getDisplayName();

        if(this.propertiesLookup.containsKey(displayName)){
            throw new InvalidParameterException(property.getDisplayName()+" is already added as a property!");
        }

        this.loadCustomizationsFor(property);

        //If the name changed then we need to check again
        if(!displayName.equals(property.getDisplayName())) {
            if (this.propertiesLookup.containsKey(property.getDisplayName())) {
                throw new InvalidParameterException(property.getDisplayName() + " is already added as a property!");
            }
        }

        this.properties.put(property.getName().toLowerCase(),property);
        this.propertiesLookup.put(property.getDisplayName().toLowerCase(), property);
    }

    @Override
    public IRareItemProperty getItemProperty(String propertyName) {
        return this.properties.get(propertyName.toLowerCase());
    }

    @Override
    public IRareItemProperty getItemPropertyByDisplayName(String propertyName) {
        return this.propertiesLookup.get(propertyName.toLowerCase());
    }

    @Override
    public IEssence createEssence(UUID creator, ItemPropertyRarity rarity) {
        return this.persistence.createEmptyEssence(creator, rarity);
    }

    @Override
    public IRareItem getRareItem(ItemStack is) {
        if (is != null && is.hasItemMeta()) {
            ItemMeta meta = is.getItemMeta();

            if (meta.hasLore()) {
                return this.getRareItem(meta.getLore());
            }
        }

        return null;
    }

    public IRareItem getRareItem(List<String> lore) {
        if (lore != null && lore.size() > 0) {
            String sIdLine = lore.get(0);

            int ccPassEnd = sIdLine.indexOf(RI4Strings.RAREITEM_CCPASS);

            if (ccPassEnd != -1) {
                int ccPassStart = sIdLine.substring(0, ccPassEnd).lastIndexOf(ChatColor.COLOR_CHAR) + 2;

                String sId = sIdLine.substring(ccPassStart, ccPassEnd);

                try {
                    int id = Integer.parseInt(sId);

                    return this.persistence.getRareItem(id);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public IRareItem getRareItem(int rareItemId) {
        return this.persistence.getRareItem(rareItemId);
    }

    @Override
    public void equipRareItem(Player player, ItemStack is) {
        IRareItem ri = this.getRareItem(is);

        if (ri != null) {
            Map<IRareItemProperty, Integer> playerActiveEffects = this.activeEffects.get(player.getUniqueId());

            if (playerActiveEffects == null) {
                playerActiveEffects = new HashMap<>();

                this.activeEffects.put(player.getUniqueId(), ri.getProperties());
            }

            for (Map.Entry<IRareItemProperty, Integer> entry : ri.getProperties().entrySet()) {
                entry.getKey().applyEffectToPlayer(player, entry.getValue());
            }

            playerActiveEffects.putAll(ri.getProperties());
        }
    }

    @Override
    public void equipRareItem(Player player, IRareItem ri) {
        Map<IRareItemProperty, Integer> playerActiveEffects = this.activeEffects.get(player.getUniqueId());

        if (playerActiveEffects == null) {
            playerActiveEffects = new HashMap<>();

            this.activeEffects.put(player.getUniqueId(), ri.getProperties());
        }

        for (Map.Entry<IRareItemProperty, Integer> entry : ri.getProperties().entrySet()) {
            entry.getKey().applyEffectToPlayer(player, entry.getValue());
        }

        playerActiveEffects.putAll(ri.getProperties());
    }

    @Override
    public void unEquipRareItem(Player player, ItemStack is) {
        IRareItem ri = this.getRareItem(is);

        if (ri != null) {
            Map<IRareItemProperty, Integer> playerActiveEffects = this.activeEffects.get(player.getUniqueId());

            if (playerActiveEffects != null) {
                for (Map.Entry<IRareItemProperty, Integer> entry : ri.getProperties().entrySet()) {
                    playerActiveEffects.remove(entry.getKey());

                    entry.getKey().removeEffectFromPlayer(player);
                }
            }
        }
    }

    @Override
    public void unEquipRareItem(Player player, IRareItem ri) {
        if (ri != null) {
            Map<IRareItemProperty, Integer> playerActiveEffects = this.activeEffects.get(player.getUniqueId());

            if (playerActiveEffects != null) {
                for (Map.Entry<IRareItemProperty, Integer> entry : ri.getProperties().entrySet()) {
                    playerActiveEffects.remove(entry.getKey());

                    entry.getKey().removeEffectFromPlayer(player);
                }
            }
        }
    }

    @Override
    public Map<IRareItemProperty, Integer> getActiveEffects(UUID uuid) {
        return this.activeEffects.get(uuid);
    }

    @Override
    public void removeActiveEffects(Player player) {
        Map<IRareItemProperty, Integer> playerEffects = this.activeEffects.remove(player.getUniqueId());

        if (playerEffects != null) {
            for (Map.Entry<IRareItemProperty, Integer> entry : playerEffects.entrySet()) {
                entry.getKey().removeEffectFromPlayer(player);
            }
        }
    }

    @Override
    public void save() {
        this.persistence.save();
    }

    @Override
    public IRareItem generateDummyRareItem(Map<IRareItemProperty, Integer> properties) {
        return new RareItem(-1, null, null, null, null, properties, RareItemStatus.DUMMY);
    }

    @Override
    public void setEssenceStatus(int id, UUID modifier, EssenceStatus status) {
        this.persistence.setEssenceStatus(id, modifier, status);
    }

    @Override
    public IEssence createEssence(UUID creator, IRareItemProperty property) {
        return this.persistence.createFilledEssence(creator, property);
    }


    @Override
    public IEssence getEssence(ItemStack is) {
        if (is != null && is.hasItemMeta()) {
            ItemMeta meta = is.getItemMeta();

            if (meta.hasLore()) {
                return this.getEssence(meta.getLore());
            }
        }

        return null;
    }

    public IEssence getEssence(List<String> lore) {
        if (lore != null && lore.size() > 1) {
            String sIdLine = lore.get(1);

            int ccPassEnd = sIdLine.indexOf(RI4Strings.ESSENCE_CCPASS);

            if (ccPassEnd != -1) {
                int ccPassStart = sIdLine.substring(0, ccPassEnd).lastIndexOf(ChatColor.COLOR_CHAR) + 2;

                String sId = sIdLine.substring(ccPassStart, ccPassEnd);

                try {
                    int id = Integer.parseInt(sId);

                    return this.persistence.getEssence(id);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    public IRareItem createRareItem(UUID creator, Map<IRareItemProperty, Integer> riProperties) {
        return this.persistence.createRareItem(creator, riProperties);
    }

    @Override
    public void setRareItemProperties(UUID modifier, int rareItemId, Map<IRareItemProperty, Integer> riProperties) {
        this.persistence.setRareItemProperties(modifier, rareItemId, riProperties);
    }

    @Override
    public void loadStrings(File stringsFile) {
        RI4Strings.RAREITEM_CCPASS = ChatColor.COLOR_CHAR + "r" + ChatColor.COLOR_CHAR + "i";
        RI4Strings.ESSENCE_CCPASS = ChatColor.COLOR_CHAR + "e" + ChatColor.COLOR_CHAR + "s";

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(stringsFile);

        for (String ri4StrKey : yml.getKeys(false)) {
            try {
                Field field = RI4Strings.class.getField(ri4StrKey);

                String ri4Str = yml.getString(ri4StrKey);

                for (ChatColor cc : ChatColor.values()) {
                    ri4Str = ri4Str.replace("&" + cc.name().toUpperCase(), cc.toString());
                }

                field.set(null, ri4Str);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                System.out.println("Invalid RI4 String: " + ri4StrKey);
                e.printStackTrace();
            }
        }
    }

    private YamlConfiguration customizationsYml;
    private void loadCustomizationsFor(IRareItemProperty rip) {
        ConfigurationSection riSection = customizationsYml.getConfigurationSection(rip.getName());

        if (riSection != null) {
            String sDisplayName = riSection.getString("displayName", null);
            if (sDisplayName != null) {
                rip.setDisplayName(sDisplayName);
            }

            String sDescription = riSection.getString("description", null);
            if (sDescription != null) {
                rip.setDescription(sDescription);
            }

            double cost = riSection.getDouble("cost", -1);
            if (cost != -1) {
                rip.setCost(cost);
            }

            String sCostType = riSection.getString("costType", null);
            if (sCostType != null) {
                rip.setCostType(PropertyCostType.valueOf(sCostType));
            }
        }
    }
}
