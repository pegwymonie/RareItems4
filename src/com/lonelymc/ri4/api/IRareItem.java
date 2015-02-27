package com.lonelymc.ri4.api;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IRareItem {
    int getId();

    Map<IRareItemProperty,Integer> getProperties();

    UUID getCreator();

    UUID getModifier();

    Date getCreated();

    Date getModified();

    RareItemStatus getStatus();
}