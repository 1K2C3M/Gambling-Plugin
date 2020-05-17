package me.Corey.Gamble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.netty.util.internal.ThreadLocalRandom;

public class Main extends JavaPlugin {

	List<Inventory> invs = new ArrayList<Inventory>();
	public static ItemStack[] contents;
	private int itemIndex = 0;
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("gamble")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("No gambling for you!!!! Be a responsible Owner...");
				return true;
			}
			Player player = (Player) sender;
			ItemStack fee = new ItemStack(Material.GOLD_INGOT);
			fee.setAmount(5);
			if (player.getInventory().getItemInMainHand().isSimilar(fee)) {
				player.getInventory().removeItem(fee);
				// OMG SPIN TIME!!!
				spin(player);
				return true;
			}
			player.sendMessage(ChatColor.DARK_RED + "You need 5 gold to gamble!");
			return true;
		}
		return false;
	}
	
	public void shuffle(Inventory inv) {
		if (contents == null) {
			ItemStack[] items = new ItemStack[10];
			
			items[0] = new ItemStack(Material.DIAMOND, 3);
			items[1] = new ItemStack(Material.DIAMOND, 3);
			items[2] = new ItemStack(Material.IRON_INGOT, 10);
			items[3] = new ItemStack(Material.IRON_INGOT, 10);
			items[4] = new ItemStack(Material.IRON_INGOT, 10);
			items[5] = new ItemStack(Material.DIRT, 16);
			items[6] = new ItemStack(Material.DIRT, 16);
			items[7] = new ItemStack(Material.DIRT, 16);
			items[8] = new ItemStack(Material.DIRT, 16);
			items[9] = new ItemStack(Material.DIRT, 16);
			
			contents = items;
		}
		
		int startingIndex = ThreadLocalRandom.current().nextInt(contents.length);
		
		for (int index = 0; index < startingIndex; index++) {
			for (int itemstacks = 9; itemstacks < 18; itemstacks++) {
				inv.setItem(itemstacks, contents[(itemstacks + itemIndex) % contents.length]);
			}
			itemIndex++;
		}
		
		// Customization
		ItemStack item = new ItemStack(Material.HOPPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + "|");
		item.setItemMeta(meta);
		inv.setItem(4, item);
	}
	
	
	public void spin(final Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "" + ChatColor.BOLD + "Gambling GUI");
		shuffle(inv);
		invs.add(inv);
		player.openInventory(inv);
		
		Random r = new Random();
		double seconds = 5.0 + (10.0 - 5.0) * r.nextDouble();
		
		new BukkitRunnable() {
			double delay = 0;
			int ticks = 0;
			boolean done = false;
			
			public void run() {
				if (done)
					return;
				ticks++;
				delay += 1 / (20 *seconds);
				if (ticks > delay * 10) {
					ticks = 0;
					
					for (int itemstacks = 9; itemstacks < 18; itemstacks++)
						inv.setItem(itemstacks, contents[(itemstacks + itemIndex) % contents.length]);
					
					itemIndex++;
					
					if (delay >= .5) {
						done = true;
						new BukkitRunnable() {
							public void run() {
								ItemStack item = inv.getItem(13);
								player.getInventory().addItem(item);
								player.updateInventory();
								player.closeInventory();
								cancel();
							}
						}.runTaskLater(Main.getPlugin(Main.class), 50);
						cancel();
					}
				}
				
			}
			
			
		}.runTaskTimer(this, 0, 1);
	}
	
}
