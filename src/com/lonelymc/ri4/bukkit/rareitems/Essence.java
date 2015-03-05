package com.lonelymc.ri4.bukkit.rareitems;

import com.lonelymc.ri4.api.EssenceStatus;
import com.lonelymc.ri4.api.IEssence;
import com.lonelymc.ri4.api.IRareItemProperty;
import com.lonelymc.ri4.api.ItemPropertyRarity;

import java.util.Date;
import java.util.UUID;

public class Essence implements IEssence {
    private final ItemPropertyRarity rarity;
    private IRareItemProperty property;
    private final int id;
    private EssenceStatus status;
    private final UUID creator;
    private final Date created;
    private UUID modifier;
    private Date modified;

    public Essence(int id, EssenceStatus status, UUID creator, Date created, UUID modifier, Date modified, ItemPropertyRarity rarity, IRareItemProperty property) {
        this.id = id;
        this.status = status;
        this.creator = creator;
        this.created = created;
        this.modifier = modifier;
        this.modified = modified;
        this.rarity = rarity;
        this.property = property;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean hasProperty() {
        return this.property != null;
    }

    @Override
    public ItemPropertyRarity getRarity() {
        if (this.property != null) {
            return this.property.getRarity();
        }
        return this.rarity;
    }

    @Override
    public IRareItemProperty getProperty() {
        return this.property;
    }

    @Override
    public EssenceStatus getStatus() {
        return this.status;
    }

    @Override
    public Date getCreated() {
        return this.created;
    }

    @Override
    public UUID getCreator() {
        return this.creator;
    }

    @Override
    public Date getModified() {
        return this.modified;
    }

    @Override
    public UUID getModifier() {
        return this.modifier;
    }

    @Override
    public String getMaterial() {
        return Essence.getMaterialByRarity(this.getRarity());
    }

    public static String getMaterialByRarity(ItemPropertyRarity rarity) {
        switch (rarity) {
            default: //case COMMON:
                return "CLAY_BALL";
            case UNCOMMON:
                return "GLOWSTONE_DUST";
            case RARE:
                return "MAGMA_CREAM";
            case LEGENDARY:
                return "NETHER_STAR";
            case STRANGE:
                return "SLIME_BALL";
        }
    }

    public void setStatus(EssenceStatus status) {
        this.status = status;
    }
}
