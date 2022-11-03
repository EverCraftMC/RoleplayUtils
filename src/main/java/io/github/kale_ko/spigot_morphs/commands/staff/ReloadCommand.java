package io.github.kale_ko.spigot_morphs.commands.staff;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import io.github.kale_ko.spigot_morphs.Main;
import io.github.kale_ko.spigot_morphs.commands.Command;
import io.github.kale_ko.spigot_morphs.util.formatting.TextFormatter;

public class ReloadCommand extends Command {
    public ReloadCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(TextFormatter.translateColors(Main.getInstance().getPluginMessages().getParsed().reload.reloading));

        Main.getInstance().reload();

        sender.sendMessage(TextFormatter.translateColors(Main.getInstance().getPluginMessages().getParsed().reload.reloaded));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Arrays.asList();
    }
}