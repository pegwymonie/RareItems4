package com.lonelymc.ri4.api;

import com.lonelymc.ri4.util.MetaStringEncoder;
import com.lonelymc.ri4.util.RomanNumeral;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Utility class to route all strings through
public class RI4Strings {
    // Note: color codes in the format &DARK_RED will be translated,
    // however you are responsible for translating any variables
    // which should be in the format !variable
    
    public static String RAREITEM_HEADER = "&DARK_GRAYRare Item &GRAY#!id";
    public static String RAREITEM_COMMON = "&AQUA!property &DARK_AQUA!level";
    public static String RAREITEM_UNCOMMON = "&GREEN!property &DARK_GREEN!level";
    public static String RAREITEM_RARE = "&RED!property &DARK_RED!level";
    public static String RAREITEM_LEGENDARY = "&GOLD!property &DARK_RED!level";
    public static String RAREITEM_STRANGE = "&LIGHT_PURPLE!property &DARK_PURPLE!level";
    public static String ESSENCE_BLANK = "&DARK_GRAY(Empty)";
    public static String ESSENCE_FILLED = "&DARK_GRAY(Filled with !property&DARK_GRAY)";
    public static String ESSENCE_COMMON = "&GRAYCommon Essence";
    public static String ESSENCE_UNCOMMON = "&AQUAUncommon Essence";
    public static String ESSENCE_RARE = "&GREENRare Essence";
    public static String ESSENCE_LEGENDARY = "&GOLDLegendary Essence";
    public static String ESSENCE_STRANGE = "&LIGHT_PURPLEStrange Essence";
    public static String ESSENCE_FOOTER_1 = "&DARK_GRAYAn essence of power";
    public static String ESSENCE_FOOTER_2 = "&GRAY/ri about &DARK_GRAYfor more info";
    
    public static String COMMAND_NOT_FROM_CONSOLE = "&REDCannot be used from the console";
    public static String COMMAND_HAT_EQUIPPED = "&GREENEnjoy your new hat!";
    public static String COMMAND_INVALID_HAT = "&REDYou can't wear that! It would look horrible and clash with your outfit";
    public static String COMMAND_MUST_HOLD_ITEM = "&REDYou must be holding an item!";
    public static String COMMAND_ADDED_PROPERTY_TO_ITEM = "&GREENAdded !property to the item in your hand";
    public static String COMMAND_INVALID_LEVEL = "&RED'!value' is not a valid level!";
    public static String COMMAND_VALUE_NOT_FOUND = "&RED'!value' not found!";
    public static String COMMAND_PLAYER_NOT_FOUND = "Player '!player' not found!";
    public static String COMMAND_IF_CONSOLE_REQUIRES_PLAYER = "If used from console you must specify a player!";
    public static String COMMAND_VALID_TYPES_ARE = "Valid types are: !types";
    public static String COMMAND_INVALID_ESSENCE_OR_PROPERTY = "!property is not a valid essence or item property type!";
    public static String COMMAND_GAVE_ESSENCE_SENDER = "&GREENGave &RESET!player &GREENa &RESET!essence &GREENessence!";
    public static String COMMAND_GAVE_ESSENCE_RECEIVER = "&GREENYou got a &RESET!essence &GREENessence!";
    public static String COMMAND_INVENTORY_WAS_FULL_ON_GROUND_NOW = "Tried to give you !item but your inventory was full so it was dropped on the ground";
    public static String COMMAND_YOU_CAN_USE = "Commands you have access to:";
    
    public static String COM_CRAFT = "craft";
    public static String COM_CRAFT_USAGE = "<property_name> [level]";
    public static String COM_CRAFT_DESC = "Add a rare item property to the item in your hand";
    public static String COM_ESSENCE = "essence";
    public static String COM_ESSENCE_USAGE = "<essence_type> [playerName]";
    public static String COM_ESSENCE_DESC = "create a rare essence";
    
    public static String LOG_RAREITEM_DISABLED = "!property not loaded because enable=false in rareitems.yml";
    public static String LOG_UNABLE_TO_SAVE_FILE = "!file could not be saved!";

    public static String getDisplayName(IEssence essence) {
        if(essence.hasProperty()){
            return essence.getProperty().getDisplayName();
        }
        
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

        lore.add(ESSENCE_FOOTER_1.replace("!id", essence.getId()+"")+MetaStringEncoder.encodeHidden(String.valueOf(essence.getId()),"es"));

        lore.add(ESSENCE_FOOTER_2);

        return lore;
    }

    public static List<String> getLore(IRareItem ri) {
        List<String> lore = new ArrayList<>();

        lore.add(RAREITEM_HEADER.replace("!id", ri.getId()+"")+MetaStringEncoder.encodeHidden(String.valueOf(ri.getId()),"ri"));

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
}
