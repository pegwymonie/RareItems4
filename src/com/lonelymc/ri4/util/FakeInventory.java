package com.lonelymc.ri4.util;

import com.lonelymc.ri4.api.RI4Strings;
import com.lonelymc.ri4.bukkit.RareItems4Plugin;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;

public class FakeInventory {
    public static String version = null;

    public static void fakeClientInventorySlot(RareItems4Plugin plugin,List<HumanEntity> viewers, final ItemStack is, final int slot) {
        switch(plugin.getNmsVersion()){
            default:
                plugin.getAPI().log(Level.WARNING, RI4Strings.NMS_CRAFTING_NOT_SUPPORTED.replace("!version", plugin.getNmsVersion()));
            case "v1_8_R1":
                com.lonelymc.ri4.nms.v1_8_R1.util.FakeInventory.fakeClientInventorySlot(plugin, viewers, is, slot);
                break;
            case "v1_8_R2":
                com.lonelymc.ri4.nms.v1_8_R2.util.FakeInventory.fakeClientInventorySlot(plugin, viewers, is, slot);
                break;
        }
    }
}
