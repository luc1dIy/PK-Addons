package wtf.jef.pk.gills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;

public class GillsPassive extends WaterAbility implements AddonAbility, PassiveAbility {
	public GillsPassive(Player player) {
		super(player);
		this.start();
	}

	@Override
	public String getAuthor() {
		return "jef~";
	}

	@Override
	public long getCooldown() {
		return 0;
	}

	@Override
	public String getDescription() {
		return "Waterbenders can obtain dissolved oxygen to breathe underwater.";
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "Gills";
	}

	@Override
	public String getVersion() {
		return "v0.0.1";
	}

	@Override
	public boolean isEnabled() {
		return ConfigManager.getConfig().getBoolean("ExtraAbilities.Jef.Gills.Enabled");
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
	}

	@Override
	public boolean isInstantiable() {
		return true;
	}

	@Override
	public boolean isProgressable() {
		return true;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public void load() {
		ProjectKorra.log.info("jef~ -- loading Gills...");

		ConfigManager.getConfig().addDefault("ExtraAbilities.Jef.Gills.Enabled", true);
		ConfigManager.defaultConfig.save();
	}

	@Override
	public void progress() {
		if (this.bPlayer.canUsePassive(this) && this.bPlayer.canBendPassive(this)
				&& this.player.getEyeLocation().getBlock().getType() == Material.WATER) {
			this.player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 10, 0, true, false, true));
		}
	}

	@Override
	public void stop() {
		ProjectKorra.log.info("jef~ -- unloading Gills...");
		super.remove();
	}

}
