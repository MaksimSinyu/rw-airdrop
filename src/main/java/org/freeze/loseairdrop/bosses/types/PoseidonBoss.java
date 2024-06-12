package org.freeze.loseairdrop.bosses.types;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.bosses.Boss;
import org.freeze.loseairdrop.bosses.abilities.Ability;
import org.freeze.loseairdrop.bosses.abilities.poseidon.LightningStrikeAbility;
import org.freeze.loseairdrop.bosses.abilities.poseidon.SpawnZombiesAbility;
import org.freeze.loseairdrop.bosses.abilities.poseidon.ThrowInAirAbility;

import java.util.ArrayList;
import java.util.List;

public class PoseidonBoss extends Boss {
    @Getter
    private String customName;
    @Getter
    private int maxHealth;
    @Getter
    private List<Ability> abilities = new ArrayList<>();

    public PoseidonBoss(JavaPlugin plugin, World world, String type) {
        super(plugin);
        abilities.add(new ThrowInAirAbility(10.0, 0.4));
        abilities.add(new SpawnZombiesAbility(world, LoseAirDrop.getINSTANCE()));
        abilities.add(new LightningStrikeAbility(plugin, 5.0, 5.0, 1));

        this.type = type;
    }
    @Override
    public void spawn(World world, Location location) {
        strikeLightning(location);
        this.entity = (LivingEntity) world.spawnEntity(location, EntityType.ZOMBIE);
        this.entity.setCustomName(ChatColor.translateAlternateColorCodes('&', customName));
        this.entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        this.entity.setHealth(maxHealth);
        this.entity.setGlowing(true);
        this.entity.setRemoveWhenFarAway(false);

        for (Ability ability : abilities) {
            if (ability instanceof LightningStrikeAbility || ability instanceof SpawnZombiesAbility) {
                ability.apply(this.getEntity());
            }
        }


        equipNetherArmor();
        equipDiamondSword();
    }

    public void equipNetherArmor() {
        Zombie zombie = (Zombie) this.entity;
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createArmorItem(Material.NETHERITE_HELMET, ChatColor.translateAlternateColorCodes('&', "&cNetherite Helmet"));
        armor[2] = createArmorItem(Material.NETHERITE_CHESTPLATE, ChatColor.translateAlternateColorCodes('&', "&cNetherite Chestplate"));
        armor[1] = createArmorItem(Material.NETHERITE_LEGGINGS, ChatColor.translateAlternateColorCodes('&', "&cNetherite Leggings"));
        armor[0] = createArmorItem(Material.NETHERITE_BOOTS, ChatColor.translateAlternateColorCodes('&', "&cNetherite Boots"));
        zombie.getEquipment().setArmorContents(armor);
    }

    public void equipDiamondSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bDiamond Sword"));
            sword.setItemMeta(meta);
        }
        this.entity.getEquipment().setItemInMainHand(sword);
    }

    @Override
    public void onDeath() {
        for (Ability ability : abilities) {
            ability.cancelTasks();
        }
    }

    private ItemStack createArmorItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    public void setHealth(int health) {
        this.maxHealth = health;
    }
}
