package io.github.kale_ko.spigot_morphs;

public class Messages {
    public static class Error {
        public String noPerms = "&cYou need the permission \"{permission}\" to do that";
        public String noConsole = "&cYou can't do that from the console";
        public String playerNotFound = "&cCouldn't find player \"{player}\"";
        public String invalidArgs = "&cInvalid arguments";
    }

    public static class Reload {
        public String reloading = "&aReloading plugin..";
        public String reloaded = "&aSuccessfully reloaded";
    }

    public Error error = new Error();

    public Reload reload = new Reload();

    public String morphed = "&aSuccessfully morphed into a{n} {entity}";
    public String unmorphed = "&aSuccessfully removed your morph";
}