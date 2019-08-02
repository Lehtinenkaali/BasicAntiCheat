package me.jinky.checks.blocks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.VersionUtil;

public class PlaceCheck extends Check {

	private static Map<Player, Map<Long, Integer>> PlaceCount = new HashMap<Player, Map<Long, Integer>>();

	@Override
	public String getEventCall() {
		return "BlockPlaceEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		if (!e.getEventName().equalsIgnoreCase(this.getEventCall())) {
			Cenix.getCenix().console("�4There was an error with cenix!");
			Cenix.getCenix().console("�4BreakCheck performCheck was called on a non-applicable event!");
			Cenix.getCenix().console("�fRequired Event: " + this.getEventCall());
			Cenix.getCenix().console("�fEvent fired upon: " + e.getEventName());
			return new CheckResult("PlaceCheck err.", true);
		}
		BlockPlaceEvent event = (BlockPlaceEvent) e;
		Player p = u.getPlayer();
		int Count = 1;
		if (PlaceCount.containsKey(p)) {
			try {
				if (PlaceCount.get(p).keySet().iterator().next() > System.currentTimeMillis()) {
					Count += PlaceCount.get(p).values().iterator().next();
				} else {
					PlaceCount.get(p).clear();
				}
			} catch (Exception excep) {
				PlaceCount.get(p).clear();
			}
		}
		if (event.getBlockPlaced().getType() == Material.SCAFFOLDING) {
			return new CheckResult("Impossible Place", true);
		}
		if (!PlaceCount.containsKey(p)) {
			PlaceCount.put(p, new HashMap<Long, Integer>());
		}
		Map<Long, Integer> R = new HashMap<Long, Integer>();
		if (PlaceCount.get(p).size() == 0) {
			R.put(System.currentTimeMillis() + 1000, Count);
		} else {
			R.put(PlaceCount.get(p).keySet().iterator().next(), Count);
		}
		PlaceCount.put(p, R);
		if (Count > 12 && !VersionUtil.hasEfficiency(p)) {
			event.setCancelled(true);
			return new CheckResult("Fast Place (12+ bps)", false);
		}
		Location placed = event.getBlockPlaced().getLocation();
		Boolean call = false;
		if (placed.distance(p.getTargetBlock(10).getLocation()) > 4.7) {
			call = true;
		}
		if (call) {
			return new CheckResult("Impossible Place", false);
		} else {
			return new CheckResult("Impossible Place", true);
		}
	}

}
