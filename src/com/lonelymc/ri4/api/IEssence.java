package com.lonelymc.ri4.api;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface IEssence {
    String getMaterial();

    int getId();

    boolean hasProperty();

    ItemPropertyRarity getRarity();

    IRareItemProperty getProperty();

    EssenceStatus getStatus();

    Date getCreated();

    UUID getCreator();

    Date getModified();

    UUID getModifier();
}
