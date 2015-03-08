package com.lonelymc.ri4.bukkit.rareitems;

import com.lonelymc.ri4.api.*;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import com.lonelymc.ri4.util.MetaStringEncoder;
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
import java.util.*;
import java.util.logging.Level;

public class RareItemsManager implements IRareItems4API {
    private final HashMap<UUID, Map<IRareItemProperty, Integer>> activeEffects;
    private final IRareItemsPersistence persistence;
    private final RareItemPropertiesManager propertiesManager;

    public RareItemsManager(RareItems4Plugin plugin, IRareItemsPersistence persistence) {
        this.activeEffects = new HashMap<>();
        
        this.propertiesManager = new RareItemPropertiesManager(plugin,this);

        this.persistence = persistence;
        this.persistence.setAPI(this);

        // Saving
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                save();
            }
        }, plugin.getConfig().getInt("save-interval"), plugin.getConfig().getInt("save-interval"));
        
        // Garbage collection
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                // Clear out any cooldowns that have passed
                for (IRareItemProperty rip : propertiesManager.getAllProperties()) {
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
    }

    @Override
    public void addItemProperty(IRareItemProperty property) {
        this.propertiesManager.addItemProperty(property);
    }

    @Override
    public IRareItemProperty getItemProperty(String propertyName) {
        return this.propertiesManager.getItemProperty(propertyName);
    }

    @Override
    public IRareItemProperty getItemPropertyByDisplayName(String propertyDisplayName) {
        return this.propertiesManager.getItemPropertyByDisplayName(propertyDisplayName);
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
            String sId = MetaStringEncoder.decodeHidden(lore.get(0),"ri");

            try {
                int id = Integer.parseInt(sId);

                return this.persistence.getRareItem(id);
            } catch (NumberFormatException ex) {
                return null;
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

        if (ri != null && ri.getStatus() != RareItemStatus.REVOKED) {
            Map<IRareItemProperty, Integer> playerActiveEffects = this.activeEffects.get(player.getUniqueId());

            if (playerActiveEffects == null) {
                playerActiveEffects = new HashMap<>();

                this.activeEffects.put(player.getUniqueId(), playerActiveEffects);
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

        if (ri != null) {// allow revoked items to unequip
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
        this.propertiesManager.save();
    }

    @Override
    public IRareItem generateDummyRareItem(Map<IRareItemProperty, Integer> properties) {
        return new RareItem(0, null, null, null, null, properties, RareItemStatus.DUMMY);
    }

    @Override
    public IEssence generateDummyEssence(ItemPropertyRarity rarity) {
        return new Essence(0,EssenceStatus.DUMMY,null,null,null,null,rarity,null);
    }

    @Override
    public IEssence generateDummyEssence(IRareItemProperty rip) {
        return new Essence(0,EssenceStatus.DUMMY,null,null,null,null,null,rip);
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
            String sId = MetaStringEncoder.decodeHidden(lore.get(1),"es");

            try {
                int id = Integer.parseInt(sId);

                return this.persistence.getEssence(id);
            } 
            catch (NumberFormatException ex) {
                return null;
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
}
