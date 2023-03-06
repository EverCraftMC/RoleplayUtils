package io.github.evercraftmc.roleplayutils.commands;

import java.lang.reflect.Field;
import java.util.List;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import io.github.evercraftmc.roleplayutils.Main;
import io.github.evercraftmc.roleplayutils.util.formatting.ComponentFormatter;
import io.github.evercraftmc.roleplayutils.util.formatting.TextFormatter;

public abstract class Command extends org.bukkit.command.Command {
    protected Command(String name, String description, List<String> aliases, String permission) {
        super(name);
        this.setLabel(name);
        this.setName(name);
        this.setDescription(description);
        this.setAliases(aliases);
        this.setPermission(permission);
        if (permission != null) {
            this.permissionMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&cYou need the permission \"" + permission + "\" to do that")));
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (this.testPermission(sender)) {
            this.run(sender, args);
        }

        return true;
    }

    @Override
    public boolean testPermission(CommandSender sender) {
        if (this.testPermissionSilent(sender)) {
            return true;
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&cYou need the permission \"" + this.getPermission() + "\" to do that")));

            return false;
        }
    }

    @Override
    public boolean testPermissionSilent(CommandSender sender) {
        return this.getPermission() == null || sender.hasPermission(this.getPermission()) || sender.isOp();
    }

    public abstract void run(CommandSender sender, String[] args);

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return tabComplete(sender, args);
    }

    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    public Command register() {
        try {
            Field commandMapField = Main.getInstance().getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Main.getInstance().getServer());
            commandMap.register(Main.getInstance().getName(), this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return this;
    }

    public void unregister() {
        if (Main.getInstance().getCommand(this.getName()) != null) {
            try {
                Field commandMapField = Main.getInstance().getServer().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                CommandMap commandMap = (CommandMap) commandMapField.get(Main.getInstance().getServer());
                Main.getInstance().getCommand(this.getName()).unregister(commandMap);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}