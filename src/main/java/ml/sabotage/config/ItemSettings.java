package ml.sabotage.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import ml.sabotage.Main;
import ml.zer0dasho.config.Config;
import ml.zer0dasho.config.format.yaml.YAMLFormat;
import ml.zer0dasho.plumber.RandomCollection;

public class ItemSettings extends Config {
	
	protected ItemSettings() {}

	public static ItemSettings load() {
		return Config.load(
				ItemSettings.class,
				new File(Main.getInstance().getDataFolder() + "/items.yml"),
				YAMLFormat.FORMATTER,
				() -> new String(Main.getInstance().getResource("items.yml").readAllBytes()));
	}
	public List<Item> items = Lists.newArrayList();
	public List<Item> special_items = Lists.newArrayList();
	
	public RandomCollection<List<ItemStack>> items() {
		return randomCollection(items);
	}
	
	public RandomCollection<List<ItemStack>> special_items() {
		return randomCollection(special_items);
	}
	
	private RandomCollection<List<ItemStack>> randomCollection(Collection<Item> items) {
		RandomCollection<List<ItemStack>> collect = new RandomCollection<>();
		items.forEach((item) -> collect.add(item.weight, item.items));
		return collect;
	}
	
	public static class Item {
		public double weight;
		public List<ItemStack> items;
		
		public Item() {}
		
		public Item(double weight, ItemStack...items) {
			this.weight = weight;
			this.items = Arrays.asList(items);
		}
	}
}