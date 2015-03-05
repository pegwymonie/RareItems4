package com.lonelymc.ri4.util;

import com.lonelymc.ri4.api.RI4Strings;
import org.bukkit.ChatColor;

public class MetaStringEncoder {
    private static String getEncodedKey(String key){
        String encodedKey = "";

        for(char c : key.toCharArray()){
            encodedKey += ChatColor.COLOR_CHAR + c;
        }
        
        return encodedKey;
    }

    /**
     * @param str String with a value encoded to it
     * @param key Key that was used when encoding the value
     * @return The encoded value
     */
    public static String decode(String str, String key){
        int end = str.indexOf(getEncodedKey(key));
        
        if(end != -1){
            int begin = str.substring(0,end).lastIndexOf(ChatColor.COLOR_CHAR)+2;
            
            return str.substring(begin,end);
        }
        
        return null;
    }

    /**
     * @param val Value to encode
     * @param color Color the text should appear as
     * @param key Ideally a 2 character string that uses characters outside of:
     *            0-9, abcdef, klmnor
     *            You can use these, however in theory you might get false positives
     *            It's probably safe to use one of those characters and one character
     *            Outside of the above range
     * @return A string that appears as color but can be retrieved with decode
     */
    public static String encode(String val, String key, ChatColor color){
        return color + val + getEncodedKey(key);
    }

    /**
     * @param key String with a value encodeHidden() to it
     * @param key Key that was used when encoding the value
     * @return The original unencoded value
     */
    public static String decodeHidden(String str,String key){
        int end = str.indexOf(getEncodedKey(key));

        if(end != -1){
            int begin = str.substring(0,end).lastIndexOf(ChatColor.COLOR_CHAR+"#")+2;

            return str.substring(begin,end);
        }

        return null;
    }

    /**
     * Note that if the string is not at the end of the metadata line, you may
     * need to append a color at the end of the returned string
     * @param val Value to hide
     * @param key Ideally a 2 character string that uses characters outside of:
     *            0-9, abcdef, klmnor
     *            You can use these, however in theory you might get false positives
     *            It's probably safe to use one of those characters and one character
     *            Outside of the above range
     * @return Hidden string
     */
    public static String encodeHidden(String val, String key){
        return ChatColor.COLOR_CHAR+"#" + getEncodedKey(val) + getEncodedKey(key);
    }
}
