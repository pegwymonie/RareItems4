package com.lonelymc.ri4.api;

import java.util.Map;
import java.util.UUID;

public interface IRareItemsPersistence {
    // I don't like this, but it makes building the rare item objects in persistence much simpler
    void setAPI(IRareItems4API api);

    // Essences
    IEssence createEmptyEssence(UUID creator, ItemPropertyRarity rarity);

    IEssence createFilledEssence(UUID creator, IRareItemProperty property);

    IEssence getEssence(int essenceId);

    void setEssenceStatus(int essenceId, UUID modifier, EssenceStatus status);

    // Rare Items
    IRareItem createRareItem(UUID creator, Map<IRareItemProperty, Integer> riProperties);

    IRareItem getRareItem(int rareItemId);

    void setRareItemProperties(UUID modifier, int rareItemId, Map<IRareItemProperty, Integer> riProperties);

    void setRareItemStatus(int rareItemId, UUID modifier, RareItemStatus status);

    void save();
}
