package wtf.jef.Glider;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityToggleGlideEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class GliderListener implements Listener {
	@EventHandler
	public void onToggleGlide(EntityToggleGlideEvent event) {
		if (event.isCancelled() || !event.isGliding() || !(event.getEntity() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity();
		final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (bPlayer != null && bPlayer.getBoundAbilityName().equalsIgnoreCase("Glider")
				&& !CoreAbility.hasAbility(player, GliderAbility.class)) {
			new GliderAbility(player);
		}
	}
}
