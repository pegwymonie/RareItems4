package com.lonelymc.ri4.bukkit.rareitems;

import com.lonelymc.ri4.api.IRareItemProperty;
import com.lonelymc.ri4.api.IRareItems4API;
import com.lonelymc.ri4.api.PropertyCostType;
import com.lonelymc.ri4.api.RI4Strings;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public class RareItemPropertiesManager {
    private final RareItems4Plugin plugin;
    private final YamlConfiguration customizationsYml;
    private final HashMap<String, IRareItemProperty> properties;
    private boolean customizationsDirty;

    public RareItemPropertiesManager(RareItems4Plugin plugin, IRareItems4API api) {
        this.plugin = plugin;

        this.customizationsYml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "properties.yml"));
        this.properties = new HashMap<>();
    }

    public Collection<IRareItemProperty> getAllProperties() {
        return this.properties.values();
    }

    public void addItemProperty(IRareItemProperty rip) {
        ConfigurationSection riSection = customizationsYml.getConfigurationSection(rip.getName());

        if (riSection == null) {
            customizationsYml.set(rip.getName() + ".enabled", true);
            customizationsYml.set(rip.getName() + ".displayName", rip.getDisplayName());
            customizationsYml.set(rip.getName() + ".description", rip.getDescription());
            customizationsYml.set(rip.getName() + ".cost", rip.getCost());
            customizationsYml.set(rip.getName() + ".costType", rip.getCostType());

            this.customizationsDirty = true;
        } else {
            if (!riSection.getBoolean("enabled")) {
                plugin.getLogger().log(Level.INFO, 
                        RI4Strings.LOG_RAREITEM_DISABLED.replace("!property", rip.getName()));

                return;
            }

            String sDisplayName = riSection.getString("displayName", rip.getDisplayName());
            if (!sDisplayName.equals(rip.getDisplayName())) {
                rip.setDisplayName(sDisplayName);
            }

            String sDescription = riSection.getString("description", rip.getDescription());
            if (!sDescription.equals(rip.getDescription())) {
                rip.setDescription(sDescription);
            }

            double cost = riSection.getDouble("cost", rip.getCost());
            if (cost != rip.getCost()) {
                rip.setCost(cost);
            }

            String sCostType = riSection.getString("costType", rip.getCostType().name());
            
            PropertyCostType costType = PropertyCostType.valueOf(sCostType);
            
            if (costType != rip.getCostType()) {
                rip.setCostType(PropertyCostType.valueOf(sCostType));
            }
        }

        this.properties.put(rip.getDisplayName().toLowerCase(), rip);
    }

    public IRareItemProperty getItemProperty(String propertyName) {
        propertyName = propertyName.toLowerCase();

        for (IRareItemProperty rip : this.properties.values()) {
            if (rip.getName().equals(propertyName)) {
                return rip;
            }
        }

        return null;
    }

    public IRareItemProperty getItemPropertyByDisplayName(String propertyDisplayName) {
        return this.properties.get(propertyDisplayName.toLowerCase());
    }

    public void save() {
        if (this.customizationsDirty) {
            try {
                this.customizationsYml.save(new File(plugin.getDataFolder(), "properties.yml"));

                this.customizationsDirty = false;
            } catch (IOException ex) {
                plugin.log(
                        Level.WARNING,
                        RI4Strings.LOG_UNABLE_TO_SAVE_FILE.replace("!file", "properties.yml"),
                        ex
                );
            }
        }
    }
}
