package no.atc.floyd.bukkit.range;


//import java.io.*;

import java.util.Date;
//import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
//import org.bukkit.entity.Player;
//import org.bukkit.Server;
//import org.bukkit.event.Event.Priority;
//import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginDescriptionFile;
//import org.bukkit.plugin.Plugin;
//import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
//import org.bukkit.material.*;


/**
* Range limiter plugin for Bukkit
*
* @author FloydATC
*/
public class RangePlugin extends JavaPlugin implements Listener {
	public static final Logger logger = Logger.getLogger("Minecraft.RangePlugin");
    //private final RangePlayerListener playerListener = new RangePlayerListener(this);
    public Integer limit = 5000;
    public ConcurrentHashMap<String,Long> LastWarning = new ConcurrentHashMap<String,Long>();;


    public void onDisable() {
        // TODO: Place any custom disable code here
    
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
    	PluginDescriptionFile pdfFile = this.getDescription();
    	logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events
    	
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        //pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Highest, this);
        //pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Highest, this);
        pm.registerEvents(this, this);

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
    	Boolean limited = false;
    	if (Math.abs(event.getTo().getBlockX()) > limit) { limited = true; }
    	if (Math.abs(event.getTo().getBlockZ()) > limit) { limited = true; }
    	if (limited && !event.getPlayer().isOp()) {
    		event.setCancelled(true);
    		String pname = event.getPlayer().getName();
    		Long last = LastWarning.get(pname);
    		if (last == null) {
    			last = 0L;
    		}
    		Long now = (new Date()).getTime() / 1000;  // Unixtime
    		if (!last.equals(now)) {
    			event.getPlayer().sendMessage("[Range] Turn back, here be dragons");
    			logger.info("[Range] Cancelled movement beyond limit of "+limit+" meters for "+event.getPlayer().getName()+" ["+last+"/"+now+"]");
    			LastWarning.put(pname, now);
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    	Boolean limited = false;
    	if (Math.abs(event.getTo().getBlockX()) > limit) { limited = true; }
    	if (Math.abs(event.getTo().getBlockZ()) > limit) { limited = true; }
    	if (limited && !event.getPlayer().isOp()) {
    		event.setCancelled(true);
    		logger.info("[Range] Cancelled teleport beyond limit of "+limit+" meters for "+event.getPlayer().getName());
    	}
    }

}
