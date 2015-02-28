package com.lonelymc.ri4.bukkit.rareitems;

import com.lonelymc.ri4.api.*;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RareItemsYMLPersistence implements IRareItemsPersistence {
    private IRareItems4API api;

    private final String essencesIndexLocation = "essencesIndexLocation";
    private final String rareItemsIndexLocation = "rareItemsIndexLocation";

    private final String saveFileName = "save.yml";
    private final File saveDir;
    private boolean saveIsDirty = false;
    private YamlConfiguration saveYml;

    private final Map<Integer, IEssence> cachedEssences;
    private final Map<Integer, IRareItem> cachedRareItems;

    public RareItemsYMLPersistence(RareItems4Plugin plugin, File saveDir) {
        this.saveDir = saveDir;

        this.cachedEssences = new HashMap<>();
        this.cachedRareItems = new HashMap<>();

        load();
    }

    @Override
    public void setAPI(IRareItems4API api){
        this.api = api;
    }

    @Override
    public IEssence createEmptyEssence(UUID creator, ItemPropertyRarity rarity) {
        int essenceId = this.saveYml.getInt(this.essencesIndexLocation);

        this.saveYml.set(this.essencesIndexLocation, essenceId + 1);

        IEssence essence = new Essence(essenceId, EssenceStatus.EMPTY, creator, new Date(), null, null, rarity, null);

        this.saveIsDirty = true;

        return essence;
    }

    @Override
    public IEssence createFilledEssence(UUID creator, IRareItemProperty property) {
        int essenceId = this.saveYml.getInt(this.rareItemsIndexLocation);

        this.saveYml.set(this.rareItemsIndexLocation, essenceId + 1);

        IEssence essence = new Essence(essenceId, EssenceStatus.FILLED, creator, new Date(), null, null, null, property);

        this.saveIsDirty = true;

        return essence;
    }

    @Override
    public IEssence getEssence(int essenceId) {
        IEssence essence = this.cachedEssences.get(essenceId);

        if (essence == null) {
            ConfigurationSection essencesSection = this.saveYml.getConfigurationSection("essences." + essenceId);

            if (essencesSection != null) {
                String propertyName = essencesSection.getString("property", null);
                ItemPropertyRarity rarity = ItemPropertyRarity.valueOf(essencesSection.getString("rarity", "COMMON"));

                EssenceStatus status = EssenceStatus.valueOf(essencesSection.getString("status"));

                String sCreator = essencesSection.getString("creator");
                UUID creator = UUID.fromString(sCreator);

                Long lCreated = essencesSection.getLong("created");
                Date created = new Date(lCreated);

                String sModifier = essencesSection.getString("modifier", null);
                UUID modifier = null;
                if (sModifier != null) {
                    modifier = UUID.fromString(sModifier);
                }

                Long lModified = essencesSection.getLong("modified", -1);
                Date modified = null;
                if (lModified != -1) {
                    modified = new Date(lCreated);
                }

                if (propertyName == null) {
                    essence = new Essence(essenceId, status, creator, created, modifier, modified, rarity, null);
                } else {
                    IRareItemProperty property = api.getItemProperty(propertyName);

                    // The reason this is done is to allow addons to be removed without causing conflicts
                    if (property == null) {
                        property = new _Dummy(propertyName);
                    }

                    essence = new Essence(essenceId, status, creator, created, modifier, modified, null, property);
                }

                this.cachedEssences.put(essenceId, essence);
            }
        }

        return essence;
    }

    @Override
    public void setEssenceStatus(int essenceId, UUID modifier, EssenceStatus status) {
        ConfigurationSection eSection = this.saveYml.getConfigurationSection("essences." + essenceId);

        if (eSection != null) {
            eSection.set("status", status.name());

            this.saveIsDirty = true;

            Essence essence = (Essence) this.cachedEssences.get(essenceId);

            if (essence != null) {
                essence.setStatus(status);
            }
        }
    }

    @Override
    public IRareItem createRareItem(UUID creator, Map<IRareItemProperty, Integer> riProperties) {
        int rareItemId = this.saveYml.getInt(rareItemsIndexLocation);

        this.saveYml.set(rareItemsIndexLocation, rareItemId + 1);

        IRareItem ri = new RareItem(rareItemId, creator, new Date(), creator, new Date(), riProperties, RareItemStatus.ACTIVE);

        String section = "rareitems." + ri.getId() + ".";

        for (Map.Entry<IRareItemProperty, Integer> entry : ri.getProperties().entrySet()) {
            saveYml.set(section + "properties." + entry.getKey().getName(), entry.getValue());
        }

        this.saveYml.set(section + "created", ri.getCreated().getTime());
        this.saveYml.set(section + "creator", ri.getCreator().toString());
        this.saveYml.set(section + "modified", ri.getCreated().getTime());
        this.saveYml.set(section + "modifier", ri.getModifier().toString());

        this.saveYml.set(section + "status", ri.getStatus().name());

        this.saveIsDirty = true;

        return ri;
    }

    @Override
    public IRareItem getRareItem(int rareItemId) {
        IRareItem ri = this.cachedRareItems.get(rareItemId);

        if (ri == null) {
            ConfigurationSection riSection = this.saveYml.getConfigurationSection("rareitems." + rareItemId);

            if (riSection != null) {
                String sCreator = riSection.getString("creator");
                UUID creator = UUID.fromString(sCreator);

                Long lCreated = riSection.getLong("created");
                Date created = new Date(lCreated);

                String sModifier = riSection.getString("modifier");
                UUID modifier = UUID.fromString(sCreator);

                Long lModified = riSection.getLong("modified");
                Date modified = new Date(lModified);

                Map<IRareItemProperty, Integer> properties = new HashMap<>();

                ConfigurationSection propertiesSection = riSection.getConfigurationSection("properties");

                for (String sPropertyName : propertiesSection.getKeys(false)) {
                    int propertyLevel = propertiesSection.getInt(sPropertyName,1);

                    IRareItemProperty property = api.getItemProperty(sPropertyName);

                    if(property == null){
                        property = new _Dummy(sPropertyName);
                    }

                    properties.put(property, propertyLevel);
                }

                RareItemStatus status = RareItemStatus.valueOf(riSection.getString("status"));

                ri = new RareItem(rareItemId, creator, created, modifier, modified, properties, status);

                this.cachedRareItems.put(rareItemId, ri);
            }
        }

        return ri;
    }

    @Override
    public void setRareItemProperties(UUID modifier, int rareItemId, Map<IRareItemProperty, Integer> riProperties) {
        ConfigurationSection riSection = this.saveYml.getConfigurationSection("rareitems." + rareItemId);

        if (riSection != null) {
            this.saveIsDirty = true;

            for (Map.Entry<IRareItemProperty, Integer> entry : riProperties.entrySet()) {
                riSection.set("properties." + entry.getKey().getName(), entry.getValue());
            }

            riSection.set("modifier", modifier.toString());
            riSection.set("modified", new Date());

            RareItem ri = (RareItem) this.cachedRareItems.get(rareItemId);

            if (ri != null) {
                ri.setProperties(riProperties);

                ri.setModifier(modifier);
                ri.setModified(new Date());
            }
        }
    }

    @Override
    public void setRareItemStatus(int rareItemId, UUID modifier, RareItemStatus status) {
        ConfigurationSection riSection = this.saveYml.getConfigurationSection("rareitems." + rareItemId);

        if (riSection != null) {
            riSection.set("status", status.name());

            this.saveIsDirty = true;

            RareItem ri = (RareItem) this.cachedRareItems.get(rareItemId);

            if (ri != null) {
                ri.setStatus(status);
            }
        }
    }

    public void load() {
        saveDir.mkdirs();

        try {
            File saveFile = new File(saveDir, this.saveFileName);

            saveYml = YamlConfiguration.loadConfiguration(saveFile);

            if (!saveFile.exists()) {
                saveYml.set(essencesIndexLocation, 1);
                saveYml.set(rareItemsIndexLocation, 1);
            }
        } catch (Exception e) {
            System.out.println("Unable to load " + this.saveFileName + "! (That's not good.)");

            e.printStackTrace();
        }
    }

    public void save() {
        if (this.saveIsDirty) {
            try {
                this.saveYml.save(new File(saveDir, this.saveFileName));

                this.saveIsDirty = false;
            } catch (IOException e) {
                Bukkit.broadcast("Unable to save " + this.saveFileName + "! (See console for additional info)", "ri4.admin.alerts");

                e.printStackTrace();
            }
        }
    }
}
