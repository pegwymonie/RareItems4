package com.lonelymc.ri4.bukkit.rareitems;

import com.lonelymc.ri4.api.IRareItemProperty;
import com.lonelymc.ri4.api.IRareItems4API;
import com.lonelymc.ri4.api.PropertyCostType;
import com.lonelymc.ri4.api.RI4Strings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public class RareItemPropertiesManager {
    private final YamlConfiguration customizationsYml;
    private final JavaPlugin plugin;
    private final HashMap<String, IRareItemProperty> properties;

    public RareItemPropertiesManager(IRareItems4API api) {

        this.customizationsYml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "rareItems.yml"));
        this.properties = new HashMap<>();
    }

    public Collection<IRareItemProperty> getAllProperties() {
        return this.properties.values();
    }

    public void addItemProperty(IRareItemProperty property) {
        ConfigurationSection riSection = customizationsYml.getConfigurationSection(property.getName());

    if (riSection == null) {
        customizationsYml.set(property.getName() + ".enabled", true);
        customizationsYml.save(new File(plugin.getDataFolder(), "rareItems.yml"));
    }
    else {
        if(!riSection.getBoolean("enabled")){
            plugin.getLogger().log(Level.INFO, RI4Strings.LOG_RAREITEM_DISABLED.replace("!property",property.getName()));

            return;
        }

        String sDisplayName = riSection.getString("displayName", null);
        if (sDisplayName != null) {
            property.setDisplayName(sDisplayName);
        }

        String sDescription = riSection.getString("description", null);
        if (sDescription != null) {
            property.setDescription(sDescription);
        }

        double cost = riSection.getDouble("cost", -1);
        if (cost != -1) {
            property.setCost(cost);
        }

        String sCostType = riSection.getString("costType", null);
        if (sCostType != null) {
            property.setCostType(PropertyCostType.valueOf(sCostType));
        }
    }

    this.propertiesLookup.put(property.getDisplayName().toLowerCase(), property);
    }

    public IRareItemProperty getItemProperty(String propertyName) {
        return this.properties.get(propertyName.toLowerCase());
    }

    public IRareItemProperty getItemPropertyByDisplayName(String propertyName) {
        propertyName = propertyName.toLowerCase();
        
        for(IRareItemProperty rip : this.properties.values()){
            if(rip.getDisplayName().equals(propertyName)){
                return rip;
            }
        }
        
        return null;
    }
}
