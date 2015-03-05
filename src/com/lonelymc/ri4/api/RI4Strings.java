package com.lonelymc.ri4.api;

import com.lonelymc.ri4.util.MetaStringEncoder;
import com.lonelymc.ri4.util.RomanNumeral;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Utility class to route all strings through
public class RI4Strings {
    // Note: color codes in the format &DARK_RED will be translated,
    // however you are responsible for translating any variables
    // which should be in the format !variable

    // Rare Items
    public static String RAREITEM_HEADER;

    public static String RAREITEM_COMMON;
    public static String RAREITEM_UNCOMMON;
    public static String RAREITEM_RARE;
    public static String RAREITEM_LEGENDARY;
    public static String RAREITEM_STRANGE;

    // Essences
    public static String ESSENCE_BLANK;
    public static String ESSENCE_FILLED;

    public static String ESSENCE_COMMON;
    public static String ESSENCE_UNCOMMON;
    public static String ESSENCE_RARE;
    public static String ESSENCE_LEGENDARY;
    public static String ESSENCE_STRANGE;

    public static String ESSENCE_FOOTER_1;
    public static String ESSENCE_FOOTER_2;

    public static String getDisplayName(IEssence essence) {
        switch (essence.getRarity()) {
            default: //case COMMON:
                return ESSENCE_COMMON;
            case UNCOMMON:
                return ESSENCE_UNCOMMON;
            case RARE:
                return ESSENCE_RARE;
            case LEGENDARY:
                return ESSENCE_LEGENDARY;
            case STRANGE:
                return ESSENCE_STRANGE;
        }
    }

    public static List<String> getItemLore(IEssence essence) {
        List<String> lore = new ArrayList<>();

        if (!essence.hasProperty()) {
            lore.add(ESSENCE_BLANK);
        } else {
            lore.add(
                    ESSENCE_FILLED.replace("!property", essence.getProperty().getDisplayName())
            );
        }

        lore.add(ESSENCE_FOOTER_1.replace("!id", MetaStringEncoder.encode(String.valueOf(essence.getId()),"es")));

        lore.add(ESSENCE_FOOTER_2);

        return lore;
    }

    public static List<String> getLore(IRareItem ri) {
        List<String> lore = new ArrayList<>();

        lore.add(RAREITEM_HEADER.replace("!id", MetaStringEncoder.encode(String.valueOf(ri.getId()),"ri")));

        for (Map.Entry<IRareItemProperty, Integer> entry : ri.getProperties().entrySet()) {
            switch (entry.getKey().getRarity()) {
                default: //case COMMON:
                    lore.add(RAREITEM_COMMON
                            .replace("!property", entry.getKey().getDisplayName())
                            .replace("!level", RomanNumeral.convertToRoman(entry.getValue())));
                    break;
                case UNCOMMON:
                    lore.add(RAREITEM_UNCOMMON
                            .replace("!property", entry.getKey().getDisplayName())
                            .replace("!level", RomanNumeral.convertToRoman(entry.getValue())));
                    break;
                case RARE:
                    lore.add(RAREITEM_RARE
                            .replace("!property", entry.getKey().getDisplayName())
                            .replace("!level", RomanNumeral.convertToRoman(entry.getValue())));
                    break;
                case LEGENDARY:
                    lore.add(RAREITEM_LEGENDARY
                            .replace("!property", entry.getKey().getDisplayName())
                            .replace("!level", RomanNumeral.convertToRoman(entry.getValue())));
                    break;
                case STRANGE:
                    lore.add(RAREITEM_STRANGE
                            .replace("!property", entry.getKey().getDisplayName())
                            .replace("!level", RomanNumeral.convertToRoman(entry.getValue())));
                    break;
            }
        }

        return lore;
    }
    
// Log messages
    public static String LOG_RAREITEM_DISABLED;
    public static String LOG_UNABLE_TO_SAVE_FILE;
}
