package org.zzzzzzz.chickenDupe;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class DupeListener implements Listener {
    private final ChickenDupe plugin;
    private final List<Material> shulkerBoxTypes = Arrays.asList(
        Material.BLUE_SHULKER_BOX,
        Material.RED_SHULKER_BOX,
        Material.BLACK_SHULKER_BOX,
        Material.PURPLE_SHULKER_BOX,
        Material.BROWN_SHULKER_BOX,
        Material.CYAN_SHULKER_BOX,
        Material.GRAY_SHULKER_BOX,
        Material.GREEN_SHULKER_BOX,
        Material.LIGHT_BLUE_SHULKER_BOX,
        Material.LIGHT_GRAY_SHULKER_BOX,
        Material.LIME_SHULKER_BOX,
        Material.MAGENTA_SHULKER_BOX,
        Material.ORANGE_SHULKER_BOX,
        Material.PINK_SHULKER_BOX,
        Material.WHITE_SHULKER_BOX,
        Material.YELLOW_SHULKER_BOX,
        Material.ORANGE_SHULKER_BOX,
        Material.MAGENTA_SHULKER_BOX,
        Material.PINK_SHULKER_BOX,
        Material.SHULKER_BOX
    );
    public DupeListener(ChickenDupe plugin){
        this.plugin = plugin;
    }
    @EventHandler
    private void onUseBox(PlayerInteractEntityEvent event){
        if (!event.getPlayer().hasPermission("chickendupe.dupe")) return;
        ItemStack shulker = event.getPlayer().getInventory().getItemInMainHand();
        if (shulkerBoxTypes.stream().anyMatch(x->x==shulker.getType())){
            if (event.getRightClicked() instanceof Chicken chicken && chicken.isAdult()){
                chicken.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 1, true, false));
                chicken.setFireTicks(1000000);
                chicken.customName(shulker.displayName());
                chicken.getEquipment().setItemInMainHand(shulker.clone());
                chicken.getEquipment().setItemInMainHandDropChance(0);
                chicken.setMetadata("justDuped",new FixedMetadataValue(plugin,false));
                shulker.setAmount(0);
                chicken.getWorld().playSound(chicken.getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
            }
        }
    }

    @EventHandler
    private void onEgg(ServerTickEndEvent event){
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Set<Chicken> chicken = new HashSet<>();
        for (Player player : players){
            chicken.addAll(player.getWorld().getEntities().stream()
                    .filter(x->x instanceof Chicken)
                    .map(x->(Chicken)x)
                    .collect(Collectors.toSet())
            );
        }
        for (Chicken c : chicken){
            if (c.getEquipment().getItemInMainHand().isEmpty()) continue;
            if (LocalTime.now().getMinute()%plugin.getConfig().getInt("dupe-interval")==0 && c.hasMetadata("justDuped")){
                if (c.getMetadata("justDuped").stream().noneMatch(x->x.value() instanceof Boolean b && !b)) continue;
                c.setMetadata("justDuped",new FixedMetadataValue(plugin,true));
                ItemStack shulker = c.getEquipment().getItemInMainHand().clone();
                c.getWorld().spawn(c.getLocation(), Item.class,item-> item.setItemStack(shulker));
                c.getWorld().playSound(c.getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
            }else{
                c.setMetadata("justDuped",new FixedMetadataValue(plugin,false));
            }
        }
    }
}
