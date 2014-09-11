package com.caved_in.commons.game.gadget;

import com.caved_in.commons.Commons;
import com.caved_in.commons.Messages;
import com.caved_in.commons.config.XmlItemStack;
import com.caved_in.commons.entity.Entities;
import com.caved_in.commons.exceptions.ProjectileCreationException;
import com.caved_in.commons.game.guns.BulletActions;
import com.caved_in.commons.game.guns.BulletBuilder;
import com.caved_in.commons.game.guns.GunProperties;
import com.caved_in.commons.item.Items;
import com.caved_in.commons.player.MinecraftPlayer;
import com.caved_in.commons.player.Players;
import com.caved_in.commons.time.TimeHandler;
import com.caved_in.commons.time.TimeType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Root(name = "projectile-gun")
public abstract class BaseGun extends ItemGadget implements Gun {
	private static final Commons commons = Commons.getInstance();

	@Element(name = "gun", type = XmlItemStack.class)
	private XmlItemStack gun;

	@Element(name = "attributes", type = GunProperties.class)
	private GunProperties properties = new GunProperties();

	private String baseName;

	private Map<UUID, Integer> ammoCounts = new HashMap<>();

	private Map<UUID, Long> shootDelays = new HashMap<>();

	private BulletBuilder builder = null;

	private BulletActions actions;

	public BaseGun(@Element(name = "gun", type = XmlItemStack.class) XmlItemStack gun, @Element(name = "attributes", type = GunProperties.class) GunProperties properties) {
		super(gun.getItemStack());
		this.gun = gun;
		this.properties = properties;
		baseName = Items.getName(gun.getItemStack());
	}

	public BaseGun(ItemStack item) {
		super(item);
		gun = XmlItemStack.fromItem(item);
		baseName = Items.getName(item);
	}

	private void initBuilder() {
		if (builder != null) {
			return;
		}

		builder = new BulletBuilder(properties.ammunition()).damage(properties.damage()).power(properties.bulletSpeed).gun(this);
	}

	@Override
	public void perform(Player holder) {
		initBuilder();

		//If the player's on cooldown from using this gun, then don't let them shoot.
		if (isOnCooldown(holder)) {
			commons.debug(holder.getName() + " is on cooldown for another " + TimeHandler.getDurationBreakdown(shootDelays.get(holder.getUniqueId()) - System.currentTimeMillis()) + "!");
			return;
		}

		//If the gadget the player has needs to be reloaded then attempt to do so.
		if (needsReload(holder)) {
			//If the gadget was reloaded then send the player a message saying it was, but via the neat-0 itemMessage system.
			if (!reload(holder)) {
				return;
			}
		}

		setAmmo(holder, getAmmo(holder) - properties.roundsPerShot);

		//We need to shoot atleast one bullet.
		if (properties.roundsPerShot <= 0) {
			properties.roundsPerShot = 1;
		}

		//Assign the shooter to the builder, used in the vector calculations.
		builder = builder.shooter(holder);

		if (properties.clusterShot) {
			//Cluster shot means it's like a shotgun, where the bullets / items all come out at once.
			for (int i = 0; i < properties.roundsPerShot; i++) {
				try {
					builder.shoot();
//					builder.spread(holder.getLocation(),properties.bulletSpread).shoot();
				} catch (ProjectileCreationException e) {
					e.printStackTrace();
				}
			}
			commons.debug("Shot a cluster of " + properties.roundsPerShot + " bullets for " + holder.getName());
		} else {
			//Because we're not using cluster shot, we're going to delay the time between each shot, so it looks like burst fire.
			for (int i = 0; i < properties.roundsPerShot; i++) {
				//Schedule each bullet to be fired with the given delay, otherwise they'd be in a cluster.
				Commons.getInstance().getThreadManager().runTaskLater(() -> {
					try {
						builder.shoot();
						//Apply new spread to the projectile gun, and then shoot that m'fucka to space and back.
//						builder.spread(holder.getLocation(), properties.bulletSpread).shoot();
					} catch (ProjectileCreationException e) {
						e.printStackTrace();
					}
				}, i * properties.bulletDelay);
			}

			commons.debug("Shot " + properties.roundsPerShot + " bullets for " + holder.getName());
		}


		//Set the player on cooldown from using this weapon.
		addCooldown(holder);

		commons.debug("Set " + holder.getName() + " on cooldown for " + properties.shotDelay + "ms");

		//Handle the on-shoot of the gun, what the item's meant to do.
		onFire(holder);
	}

	public boolean reload(final Player player) {
		if (!needsReload(player)) {
			return false;
		}

		UUID id = player.getUniqueId();

		final MinecraftPlayer mcPlayer = Players.getData(id);

		//If the player's already reloading, then don't bother with reloading again.
		if (mcPlayer.isReloading()) {
			return false;
		}

		mcPlayer.setReloading(properties.reloadSpeed);

		//Reload according to the guns reload speed!
		commons.getThreadManager().runTaskLater(() -> {
			if (ammoCounts.containsKey(id)) {
				Players.sendMessage(player, Messages.gadgetReloaded(this));
			}

			setAmmo(player, properties.clipSize);
			//The player's no-longer reloading, so remove them from doing so.
			mcPlayer.setReloading(0);
		}, TimeHandler.getTimeInTicks(properties.reloadSpeed, TimeType.SECOND));
		return true;
	}

	private void addCooldown(Player player) {
		shootDelays.put(player.getUniqueId(), System.currentTimeMillis() + properties.shotDelay);
	}

	private boolean isOnCooldown(Player player) {
		UUID id = player.getUniqueId();
		if (!shootDelays.containsKey(id)) {
			return false;
		}

		return shootDelays.get(id) >= System.currentTimeMillis();
	}

	public boolean needsReload(Player player) {
		UUID id = player.getUniqueId();
		return ammoCounts.containsKey(id) && ammoCounts.get(id) <= 0;
		/*
		ItemStack item = getItem();
		if (!Players.hasItemInHand(player,item)) {
			return false;
		}

		ItemStack guns = player.getItemInHand();
		String gunName = Items.getName(guns);
		if (!StringUtil.startsWithIgnoreCase(gunName,baseName)) {
			Commons.getInstance().debug("Base item isn't a guns... Weird!","Base Name Desired: " + baseName, "Name given: " + Items.getName(guns));
			return false;
		}

		String[] nameSplit = gunName.split(baseName);

		if (nameSplit.length <= 1) {
			return false;
		}

		String bulletSide = nameSplit[1];
		String bulletsCount = StringUtils.substringBetween(bulletSide,"<",">");
		if (!NumberUtils.isNumber(bulletsCount)) {
			Commons.getInstance().debug(bulletsCount + " is not a valid number!");
			return false;
		}

		int bulletCount = Integer.parseInt(bulletsCount);
		return bulletCount <= 0;
		  */

	}

	public void damage(LivingEntity damaged, Player shooter) {
		Entities.damage(damaged, properties.bulletDamage * properties.roundsPerShot, shooter);
		actions.onHit(damaged);
	}


	@Override
	public BulletActions getBulletActions() {
		return actions;
	}

	@Override
	public void setBulletActions(BulletActions actions) {
		this.actions = actions;
	}

	public GunProperties attributes() {
		return properties;
	}

	public void attributes(GunProperties properties) {
		this.properties = properties;
	}

	public int getAmmo(Player player) {
		UUID id = player.getUniqueId();
		if (!ammoCounts.containsKey(id)) {
			ammoCounts.put(id, properties.clipSize);
		}
		return ammoCounts.get(player.getUniqueId());
	}

	public void setAmmo(Player player, int amt) {
		ammoCounts.put(player.getUniqueId(), amt);
	}

	public abstract void onFire(Player shooter);
}
