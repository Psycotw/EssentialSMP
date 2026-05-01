package org.psyco.essentialSmp.manager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager{
    private final Map<UUID, UUID> tpaRequests;

    public TpaManager(Map<UUID, UUID> tpaRequests) {
        this.tpaRequests = tpaRequests;
    }

    public void sendTpaRequest(Player sender, Player target){


        tpaRequests.put(target.getUniqueId(), sender.getUniqueId());

        target.sendMessage(sender.getName() + " ti ha mandato una richiesta di tpa");

        TextComponent accept = new TextComponent("[ACCETTA]");
        accept.setColor(ChatColor.GREEN);
        accept.setBold(true);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));


        TextComponent deny = new TextComponent("[RIFIUTA]");
        deny.setColor(ChatColor.RED);
        deny.setBold(true);
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        TextComponent message = new TextComponent();
        message.addExtra(accept);
        message.addExtra(" ");
        message.addExtra(deny);

        target.spigot().sendMessage(message);
        sender.sendMessage("richiesta di tpa mandata a " + target.getName());



    }

    public Map<UUID, UUID> getTpaRequests() {
        return tpaRequests;
    }


}
