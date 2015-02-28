package com.lonelymc.ri4.bukkit.listeners;

import com.lonelymc.ri4.api.IEssence;
import com.lonelymc.ri4.api.IRareItem;
import com.lonelymc.ri4.api.IRareItemProperty;
import com.lonelymc.ri4.api.IRareItems4API;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import com.lonelymc.ri4.rareitems.EssenceStatus;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
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

    @EventHandler(ignoreCancelled=true)
    public void onCraftingInteract(InventoryClickEvent e){
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

                            fakeClientInventorySlot(e.getViewers(), isAir, i);
                        }
                    }

                    ItemStack isRareItem = itemToAddTo.clone();

                    isRareItem.setAmount(1);

                    Player creator = (Player) e.getWhoClicked();

                    IRareItem ri = this.api.createRareItem(creator.getUniqueId(), properties);

                    ItemMeta meta = isRareItem.getItemMeta();

                    meta.setLore(ri.getLore());

                    isRareItem.setItemMeta(meta);

                    for(IEssence essence : essences){
                        this.api.setEssenceStatus(essence.getId(), creator.getUniqueId(), EssenceStatus.USED);
                    }

                    e.setCurrentItem(isRareItem);

                    fakeClientInventorySlot(e.getViewers(), isRareItem, 0);
                }
                // Recipe click
                else {
                    IRareItem dummyRi = this.api.generateDummyRareItem(properties);

                    ItemStack isDummyRareItem = itemToAddTo.clone();

                    isDummyRareItem.setAmount(1);

                    ItemMeta meta = isDummyRareItem.getItemMeta();

                    meta.setLore(dummyRi.getLore());

                    isDummyRareItem.setItemMeta(meta);

                    fakeClientInventorySlot(e.getViewers(), isDummyRareItem, 0);
                }
            }
        }
    }

    public void fakeClientInventorySlot(List<HumanEntity> viewers, ItemStack is, int slot) {
        // Generally it's actually just the player
        for(HumanEntity viewer : viewers) {
            if (viewer instanceof CraftPlayer) {
                Bukkit.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        EntityPlayer handle = ((CraftPlayer) viewer).getHandle();

                        if (handle.activeContainer != null) {
                            handle.playerConnection.sendPacket(
                                    new PacketPlayOutSetSlot(
                                            handle.activeContainer.windowId,
                                            slot,
                                            CraftItemStack.asNMSCopy(is)
                                    )
                            );
                        }
                    }
                }, 1);
            }
        }
    }
}