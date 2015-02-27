package com.lonelymc.ri4.bukkit.rareitems;

import com.lonelymc.ri4.api.IRareItem;
import com.lonelymc.ri4.api.IRareItemProperty;
import com.lonelymc.ri4.api.RareItemStatus;
import net.md_5.bungee.api.ChatColor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RareItem implements IRareItem {
    private final int id;
    private Map<IRareItemProperty, Integer> properties;
    private final UUID creator;
    private final Date created;
    private Date modified;
    private UUID modifier;
    private RareItemStatus status;

    public RareItem(int id, UUID creator, Date created, UUID modifier, Date modified, Map<IRareItemProperty, Integer> properties, RareItemStatus status) {
        this.id = id;
        this.creator = creator;
        this.created = created;
        this.modifier = modifier;
        this.modified = modified;
        this.properties = properties;
        this.status = status;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<IRareItemProperty, Integer> getProperties() {
        Map<IRareItemProperty, Integer> temp = new HashMap<>();

        temp.putAll(this.properties);

        return temp;
    }

    @Override
    public UUID getCreator() {
        return this.creator;
    }

    @Override
    public UUID getModifier() {
        return this.modifier;
    }

    @Override
    public Date getCreated() {
        return this.created;
    }

    @Override
    public Date getModified() {
        return this.modified;
    }

    public void setStatus(RareItemStatus status) {
        this.status = status;
    }

    public RareItemStatus getStatus() {
        return this.status;
    }

    public void setModifier(UUID modifier) {
        this.modifier = modifier;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public void setProperties(Map<IRareItemProperty, Integer> properties) {
        this.properties = properties;
    }
}
