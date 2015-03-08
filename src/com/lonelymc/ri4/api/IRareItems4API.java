package com.lonelymc.ri4.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IRareItems4API {
    //notification of plugin being shutdown
    void save();

    // Item Properties
    void addItemProperty(IRareItemProperty property);

    IRareItemProperty getItemProperty(String propertyName);

    IRareItemProperty getItemPropertyByDisplayName(String propertyName);

    // Essences
    IEssence createEssence(UUID creator, ItemPropertyRarity rarity);

    IEssence createEssence(UUID creator, IRareItemProperty property);

    IEssence generateDummyEssence(IRareItemProperty rip);

    void setEssenceStatus(int id, UUID modifier, EssenceStatus status);

    // Rare Items
    IRareItem createRareItem(UUID creator, Map<IRareItemProperty,Integer> riProperties);

    void setRareItemProperties(UUID modifier, int rareItemId, Map<IRareItemProperty, Integer> riProperties);

    // For recipes, displays, etc.
    IEssence generateDummyEssence(ItemPropertyRarity rarity);
    
    IRareItem generateDummyRareItem(Map<IRareItemProperty, Integer> properties);

    //Active effects
    Map<IRareItemProperty,Integer> getActiveEffects(UUID uniqueId);

    // Bukkit specific methods
    IEssence getEssence(ItemStack is);

    IRareItem getRareItem(ItemStack is);

    IRareItem getRareItem(int rareItemId);

    void equipRareItem(Player player, ItemStack is);

    void equipRareItem(Player player, IRareItem ri);

    void unEquipRareItem(Player player, ItemStack is);

    void unEquipRareItem(Player player, IRareItem ri);

    void removeActiveEffects(Player player);

    Collection<IRareItemProperty> getAllItemProperties();

    void setRecipeForProperty(String propertyName, String[] recipe);

    boolean isDummyEssence(ItemStack is);
}
