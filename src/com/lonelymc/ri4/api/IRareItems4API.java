package com.lonelymc.ri4.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface IRareItems4API {
    //notification of plugin being shutdown
    void save();

    // Item Properties
    void addItemProperty(IRareItemProperty property);

    IRareItemProperty getItemProperty(String propertyName);

    IRareItemProperty getItemPropertyByDisplayName(String propertyName);

    void saveItemProperty(IRareItemProperty rip);

    // Essences
    IEssence createEssence(UUID creator, ItemPropertyRarity rarity);

    IEssence createEssence(UUID creator, IRareItemProperty property);

    IEssence getEssence(ItemStack is);

    IEssence getEssence(int id);

    void saveEssence(IEssence essence);

    IEssence generateDummyEssence(IRareItemProperty rip);

    IEssence generateDummyEssence(ItemPropertyRarity rarity);

    boolean isDummyEssence(ItemStack is);

    // Rare Items
    IRareItem createRareItem(UUID creator, Map<IRareItemProperty,Integer> riProperties);

    IRareItem getRareItem(ItemStack is);

    IRareItem getRareItem(int rareItemId);

    void saveRareItem(IRareItem ri);

    IRareItem generateDummyRareItem(Map<IRareItemProperty, Integer> properties);

    //Effects management
    Map<IRareItemProperty,Integer> getActiveEffects(UUID uniqueId);

    void equipRareItem(Player player, ItemStack is);

    void equipRareItem(Player player, IRareItem ri);

    void unEquipRareItem(Player player, ItemStack is);

    void unEquipRareItem(Player player, IRareItem ri);

    void removeActiveEffects(Player player);

    Collection<IRareItemProperty> getAllItemProperties();
}
