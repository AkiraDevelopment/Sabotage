package ml.zer0dasho.plumber.utils;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import ml.zer0dasho.plumber.utils.builders.ItemBuilder;

/**
 * The SprinklerTools utility class.
 * @author 0-o#9646
 */
public class Sprink {
	
	/**
	 * Retrieves the most frequent value of a map.
	 * You can use this to implement voting systems.
	 * @param map The map to get the most frequent value of.
	 */
    public static <K,V> V mostFrequentValue(Map<K,V> map) {
    	Collection<V> val = map.values();
    	Entry<K, V> entry = map.entrySet().stream().max((a, b) -> Collections.frequency(val, b.getValue()) - Collections.frequency(val, a.getValue())).get();
    	return entry.getValue();
    }
    
    /**
     * Retrieves a random element from a list.
     * 
     * @param remove - if true, it removes the random element from the list
     */
    public static <T> T randomElement(Collection<T> list, boolean remove) {
    	@SuppressWarnings("unchecked")
			T element = (T) list.toArray()[new Random().nextInt(list.size())];
    	
    	if(remove)    		
    		list.remove(element);

    	return element;
    }
    
	/**
	 * Parses an Object as a Number.
	 * If the provided Object isn't an instance of Number, this returns -1.
	 *
	 */
	public static Number number(Object object) {
		if(object instanceof Double)  return (double) object;
		if(object instanceof Float)   return (float) object;
		if(object instanceof Integer) return (int) object;

		return -1;
	}
    
	/**
	 * Convert a string with  '&' encoded colors to a ChatColor encoded string.
	 * @param text - text to encode
	 */
	public static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}	
	
	/**
	 * Convert a list of strings with  '&' encoded colors to a ChatColor encoded list of strings.
	 * @param list - List of strings to encode
	 */
	public static List<String> color(List<String> list) {
		list.replaceAll(Sprink::color);
		
		return list;
	}
	
	/**
	 * Convert an array of strings with  '&' encoded colors to a ChatColor encoded array of strings.
	 * @param text - Array of strings to encode
	 */
	public static String[] color(String...text) {
		for(int i = 0; i < text.length; i++)
			text[i] = color(text[i]);

		return text;
	}
    
	/**
	 * Clears a player's inventory.
	 * 
	 * @param removeArmor - if true, the player's armor is also removed.
	 */
    public static void clearInventory(Player player, boolean removeArmor) {
    	player.getInventory().clear();
    	if(removeArmor) player.getInventory().setArmorContents(null);
    }
    
    /**
     * Removes potion effects from a player.
     *
	 */
    public static void clearEffects(Player player) {
    	player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }
    
    /**
     * Same as 'new ItemBuilder(material)'
     *
	 */
    public static ItemBuilder itemBuilder(Material material) {
    	return new ItemBuilder(material);
    }
}