package com.lonelymc.ri4.bukkit.rareitems;

import com.lonelymc.ri4.api.*;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import com.lonelymc.ri4.util.ItemStackConvertor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
            
            if(rip.getRecipe() != null){
                customizationsYml.set(rip.getName() + ".recipe", rip.getRecipe());
            }

            this.customizationsDirty = true;
        } 
        else {
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

            List<String> recipe = riSection.getStringList("recipe");
            if(recipe != null){
                customizationsYml.set(rip.getName() + ".recipe", recipe);
            }
        }

        this.properties.put(rip.getDisplayName().toLowerCase(), rip);

        Iterator<Recipe> it = plugin.getServer().recipeIterator();
        
        String recipeName = ChatColor.COLOR_CHAR+"#"+rip.getName();
        
        while (it.hasNext()) {
            Recipe r = it.next();

            ItemStack result = r.getResult();
            
            if (result.getType().equals(Material.DIRT) && result.hasItemMeta()) {
                ItemMeta meta = result.getItemMeta();
                

            }
        }
        
        // AFAIK there's no recipe for dirt, so it makes a good early conditional
        ItemStack isPlaceholder = new ItemStack(Material.DIRT);

        ItemMeta meta = isPlaceholder.getItemMeta();

        meta.setDisplayName(recipeName);
        
        isPlaceholder.setItemMeta(meta);
        
        ShapedRecipe recipe = new ShapedRecipe(isPlaceholder);

        String[] ripRecipe = rip.getRecipe();

        char[] availableRecipeChars = new char[]{'A','B','C','D','E','F','G','H','I'};
        String recipeChars = "";
        
        Map<Material,Character> recipeShape = new HashMap<>();
        
        for(String ingredient : ripRecipe){
            Character sChar;
            
            switch(ingredient) {
                default:
                    ItemStack is = ItemStackConvertor.fromString(ingredient);

                    sChar = recipeShape.get(is.getType());

                    if (sChar == null) {
                        recipeShape.put(is.getType(), availableRecipeChars[recipeShape.size()]);
                    }

                    recipeChars += sChar;

                    break;
                case "AIR":
                    recipeChars += " ";
                    break;

                case "!COMMON_ESSENCE":
                case "!UNCOMMON_ESSENCE":
                case "!RARE_ESSENCE":
                case "!LEGENDARY_ESSENCE":
                case "!STRANGE_ESSENCE":
                    String rarity = ingredient.substring(1, ingredient.indexOf("_"));

                    Material m = Material.valueOf(Essence.getMaterialByRarity(ItemPropertyRarity.valueOf(rarity)));

                    sChar = recipeShape.get(m);

                    if (sChar == null) {
                        recipeShape.put(m, availableRecipeChars[recipeShape.size()]);
                    }

                    recipeChars += sChar;

                    break;
            }
        }
        
        while(recipeChars.length() < 9){
            recipeChars += " ";
        }
        
        recipe.shape(
                recipeChars.substring(0,3),
                recipeChars.substring(3,6),
                recipeChars.substring(6)
        );
        
        for(Map.Entry<Material,Character> entry : recipeShape.entrySet()){
           recipe.setIngredient(entry.getValue(), entry.getKey());
        }
        
        plugin.getServer().addRecipe(recipe);
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
