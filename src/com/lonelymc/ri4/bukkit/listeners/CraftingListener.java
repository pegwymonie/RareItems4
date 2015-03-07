package com.lonelymc.ri4.bukkit.listeners;

import com.lonelymc.ri4.api.*;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import com.lonelymc.ri4.util.FakeInventory;
import com.lonelymc.ri4.util.MetaStringEncoder;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.PacketPlayOutSetSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingListener implements Listener {
    private final IRareItems4API api;
    private final RareItems4Plugin plugin;

    public CraftingListener(RareItems4Plugin plugin, IRareItems4API api){
        this.plugin = plugin;
        this.api = api;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onCompletedEssenceRecipe(CraftItemEvent e){
        ItemStack isResult = e.getInventory().getItem(0);

        if(isResult.getType().equals(Material.DIRT) && isResult.hasItemMeta()){
            ItemMeta meta = isResult.getItemMeta();

            String propertyName = MetaStringEncoder.decodeHidden(meta.getDisplayName(),"rir");

            if(propertyName != null) {
                IRareItemProperty rip = this.api.getItemProperty(propertyName);

                if(rip != null){
                    IEssence essence = this.api.generateDummyEssence(rip);
                    
                    ItemStack isDummyEssence = new ItemStack(Material.valueOf(essence.getMaterial()));

                    ItemMeta dummyMeta = isDummyEssence.getItemMeta();

                    dummyMeta.setDisplayName(RI4Strings.getDisplayName(essence));
                    dummyMeta.setLore(RI4Strings.getItemLore(essence));
                    
                    isDummyEssence.setItemMeta(meta);
                    
                    FakeInventory.fakeClientInventorySlot(
                            this.plugin,
                            e.getViewers(),
                            isDummyEssence, 
                            0
                    );
                    
                    return;
                }
            }

            e.getInventory().setResult(null);
        }
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onCraftRareEssence(PrepareItemCraftEvent e){
        ItemStack result = e.getRecipe().getResult();
        
        if(result.getType().equals(Material.DIRT) && result.hasItemMeta()){
            ItemMeta meta = result.getItemMeta();

            String propertyName = MetaStringEncoder.decodeHidden(meta.getDisplayName(),"rir");

            if(propertyName != null) {
                IRareItemProperty rip = this.api.getItemProperty(propertyName);
                
                if(rip != null){
                    IEssence essence = this.api.createEssence(e.getViewers().get(0).getUniqueId(),rip);
                    
                    ItemStack isEssence = new ItemStack(Material.valueOf(essence.getMaterial()));

                    ItemMeta essenceMeta = isEssence.getItemMeta();

                    essenceMeta.setDisplayName(RI4Strings.getDisplayName(essence));
                    
                    essenceMeta.setLore(RI4Strings.getItemLore(essence));

                    isEssence.setItemMeta(essenceMeta);
                    
                    e.getInventory().setResult(isEssence);
                    
                    return;
                }
            }

            e.getInventory().setResult(null);
        }
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onCraftingEssenceClick(InventoryClickEvent e){
        if(e.getInventory().getType().equals(InventoryType.WORKBENCH)){

            if(e.getRawSlot() < 10) {
                ItemStack itemToAddTo = null;
                Map<IRareItemProperty,Integer> properties = new HashMap<>();
                List<IEssence> essences = new ArrayList<IEssence>();
                int iAddItemToSlot = -1;

                for(int i=1;i<10;i++){
                    ItemStack is = e.getInventory().getItem(i);

                    // pretend the item is already in the recipe
                    if(i == e.getRawSlot()){
                        is = e.getCursor();
                    }

                    if(is != null && !is.getType().equals(Material.AIR)){
                        IEssence essence = this.api.getEssence(is);

                        // Allow essences and one item to add the essences to
                        if(essence != null){
                            if(essence.getProperty() != null){
                                Integer level = properties.get(essence.getProperty());

                                if(level == null){
                                    level = 1;
                                }
                                else {
                                    level++;
                                }

                                properties.put(essence.getProperty(),level);
                            }
                            else {
                                // can't create a dynamic recipe with a blank essence
                                return;
                            }

                            essences.add(essence);
                        }
                        else if(itemToAddTo == null){
                            itemToAddTo = is;
                            iAddItemToSlot = i;
                        }
                        else {
                            return;
                        }
                    }
                }

                if(properties.isEmpty() || itemToAddTo == null){
                    return;
                }


                // Result click
                if (e.getRawSlot() == 0) {
                    for(int i=1;i<10;i++){
                        ItemStack is = e.getInventory().getItem(i);

                        if(is != null && !is.getType().equals(Material.AIR)) {
                            ItemStack isAir = new ItemStack(Material.AIR);

                            e.getInventory().setItem(i,isAir);

                            FakeInventory.fakeClientInventorySlot(this.plugin,e.getViewers(), isAir, i);
                        }
                    }

                    ItemStack isRareItem = itemToAddTo.clone();

                    isRareItem.setAmount(1);

                    Player creator = (Player) e.getWhoClicked();

                    IRareItem ri = this.api.createRareItem(creator.getUniqueId(), properties);

                    ItemMeta meta = isRareItem.getItemMeta();

                    meta.setLore(RI4Strings.getLore(ri));

                    isRareItem.setItemMeta(meta);

                    for(IEssence essence : essences){
                        this.api.setEssenceStatus(essence.getId(), creator.getUniqueId(), EssenceStatus.USED);
                    }

                    e.setCurrentItem(isRareItem);

                    FakeInventory.fakeClientInventorySlot(this.plugin,e.getViewers(), isRareItem, 0);
                }
                // Recipe click
                else {
                    IRareItem dummyRi = this.api.generateDummyRareItem(properties);

                    ItemStack isDummyRareItem = itemToAddTo.clone();

                    isDummyRareItem.setAmount(1);

                    ItemMeta meta = isDummyRareItem.getItemMeta();

                    meta.setLore(RI4Strings.getLore(dummyRi));

                    isDummyRareItem.setItemMeta(meta);

                    FakeInventory.fakeClientInventorySlot(this.plugin,e.getViewers(), isDummyRareItem, 0);
                }
            }
        }
    }
}