package io.github.kale_ko.spigot_morphs.util.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import io.github.kale_ko.spigot_morphs.util.formatting.TextFormatter;

public class SerializableItemStack {
    private Material type = Material.STONE;
    private String name = "Stone";
    private Integer amount = 1;
    private Integer damage = 0;

    private List<String> lore = new ArrayList<String>();
    private List<SerializableEnchantment> enchantments = new ArrayList<SerializableEnchantment>();

    private Integer customModelData = 0;

    public SerializableItemStack(Material type, String name, Integer amount, Integer damage, List<String> lore, List<SerializableEnchantment> enchantments, Integer customModelData) {
        this.type = type;
        this.name = name;
        this.amount = amount;
        this.damage = damage;

        this.lore = lore;
        this.enchantments = enchantments;

        this.customModelData = customModelData;
    }

    public Material getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public Integer getDamage() {
        return this.damage;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public List<SerializableEnchantment> getEnchantments() {
        return this.enchantments;
    }

    public Integer getCustomModelData() {
        return this.customModelData;
    }

    public ItemStack toBukkitItemStack() {
        ItemStack stack = new ItemStack(this.getType(), this.getAmount());
        ItemMeta meta = stack.getItemMeta();

        if (this.getName() != null) {
            meta.setDisplayName(TextFormatter.translateColors(this.getName()));
        }

        if (this.getDamage() != null && meta instanceof Damageable damageable) {
            damageable.setDamage(this.getDamage());
        }

        if (this.getLore().size() > 0) {
            meta.setLore(this.getLore());
        }

        for (SerializableEnchantment enchantment : this.getEnchantments()) {
            meta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), true);
        }

        if (this.getCustomModelData() != null) {
            meta.setCustomModelData(this.getCustomModelData());
        }

        stack.setItemMeta(meta);

        return stack;
    }

    public static SerializableItemStack fromBukkitItemStack(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        List<String> lore = new ArrayList<String>();

        if (meta.hasLore()) {
            lore = meta.getLore();
        }

        List<SerializableEnchantment> enchantments = new ArrayList<SerializableEnchantment>();

        if (meta.hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> enchantment : meta.getEnchants().entrySet()) {
                enchantments.add(SerializableEnchantment.fromBukkitEnchantment(enchantment.getKey(), enchantment.getValue()));
            }
        }

        return new SerializableItemStack(stack.getType(), meta.hasDisplayName() ? meta.getDisplayName() : null, stack.getAmount(), meta instanceof Damageable ? ((Damageable) meta).getDamage() : null, lore, enchantments, meta.getCustomModelData());
    }
}