package wtf.jef.Glider;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.MultiAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager.MultiAbilityInfoSub;
import com.projectkorra.projectkorra.configuration.ConfigManager;

public class GliderAbility extends AirAbility implements AddonAbility, MultiAbility {
	public static enum gliderMode {
		REGULAR, PROPEL, HOVER, EXIT
	}

	public static GliderListener gliderListener;

	public gliderMode currentMode;

	// hard-coded hover effect radius yeah<3
	private double radius = 2.5;
	private int degree = 0;
	
	// options
	private long cooldown;
	private double propelMultiplier;

	public GliderAbility(Player player) {
		super(player);

		if (this.bPlayer.isOnCooldown(this) || !this.bPlayer.canBend(this)) {
			return;
		}

		final FileConfiguration config = ConfigManager.getConfig();
		this.cooldown = config.getLong("ExtraAbilities.Jef.Glider.Cooldown");
		this.propelMultiplier = config.getDouble("ExtraAbilities.Jef.Glider.PropelMultiplier");
		
		MultiAbilityManager.bindMultiAbility(this.player, "Glider");
		this.player.getInventory().setHeldItemSlot(1);
		this.start();
	}
	
	@Override
	public String getAuthor() {
		return "jef~";
	}

	@Override
	public long getCooldown() {
		return this.cooldown;
	}

	public String getDescription() {
		return "Airbenders can enhance their gliders by manipulating the currents around them.";
	}

	public String getInstructions() {
		return "Start gliding on the ability's slot, then cycle through the modes by scrolling.\nModes: Regular, Propel, Hover, Exit";
	}

	@Override
	public Location getLocation() {
		return this.player.getLocation();
	}

	@Override
	public ArrayList<MultiAbilityInfoSub> getMultiAbilities() {
		final ArrayList<MultiAbilityInfoSub> subAbilities = new ArrayList<MultiAbilityInfoSub>();
		subAbilities.add(new MultiAbilityInfoSub("Regular", Element.AIR));
		subAbilities.add(new MultiAbilityInfoSub("Propel", Element.AIR));
		subAbilities.add(new MultiAbilityInfoSub("Hover", Element.AIR));
		subAbilities.add(new MultiAbilityInfoSub("Exit", Element.FIRE)); // thought this'd be cute ... just for the visuals

		return subAbilities;
	}

	@Override
	public String getName() {
		return "Glider";
	}

	@Override
	public String getVersion() {
		return "v0.0.2";
	}

	public boolean isEnabled() {
		FileConfiguration config = ConfigManager.getConfig();
		config.addDefault("ExtraAbilities.Jef.Glider.Enabled", true);
		
		ConfigManager.defaultConfig.save();
		
		return config.getBoolean("ExtraAbilities.Jef.Glider.Enabled");
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}
	
	@Override
	public void load() {
		FileConfiguration config = ConfigManager.getConfig();

		config.addDefault("ExtraAbilities.Jef.Glider.Cooldown", 0);
		config.addDefault("ExtraAbilities.Jef.Glider.PropelMultiplier", 0.05);

		ConfigManager.defaultConfig.save();
		
		GliderAbility.gliderListener = new GliderListener();
		Bukkit.getServer().getPluginManager().registerEvents(GliderAbility.gliderListener, ProjectKorra.plugin);

		ProjectKorra.log.info("jef~ -- loaded " + this.getName());
	}

	@Override
	public void progress() {
		if (!this.player.isOnline() || (!this.player.isGliding() && this.currentMode != gliderMode.HOVER)
				|| (this.currentMode == gliderMode.HOVER
						&& this.player.getLocation().getBlock().getType() != Material.AIR)) {
			this.remove();
			return;
		}

		this.currentMode = gliderMode.values()[this.player.getInventory().getHeldItemSlot()];

		switch (this.currentMode) {
		case HOVER:
			this.player.setGliding(false);
			this.player.setFlying(false);

			final Vector velocity = this.player.getVelocity().clone();
			this.player.setVelocity(new Vector(velocity.getX(), 0, velocity.getZ()));
			this.player.addPotionEffect(
					new PotionEffect(PotionEffectType.SLOW_FALLING, 10, Integer.MAX_VALUE, true, false, false));

			AirAbility.playAirbendingParticles(this.player.getEyeLocation().clone().add(
					Math.cos(this.degree) * this.radius, 0.5 - Math.random(), Math.sin(this.degree) * this.radius), 3);
			this.degree = this.degree >= 90 ? 0 : this.degree + 1;

			return;
		case PROPEL:
			AirAbility.playAirbendingParticles(this.player.getLocation(), 3, Math.random(), 0, Math.random());

			final float yaw = (float) Math.toRadians(this.player.getLocation().getYaw());
			this.player.setVelocity(this.player.getVelocity()
					.add(new Vector(-Math.sin(yaw) * this.propelMultiplier, 0, Math.cos(yaw) * this.propelMultiplier)));
			break;
		case REGULAR:
			break;
		case EXIT:
			this.remove();
			return;
		}

		this.player.setGliding(true);
	}

	@Override
	public void remove() {
		this.player.setGliding(false);
		MultiAbilityManager.unbindMultiAbility(this.player);
		this.bPlayer.addCooldown(this);
		super.remove();
	}

	@Override
	public void stop() {
		super.remove();
		HandlerList.unregisterAll(GliderAbility.gliderListener);
		ProjectKorra.log.info("jef~ -- stopped " + this.getName());
	}

}
