package tech.inudev.profundus.database;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import tech.inudev.profundus.database.DatabaseUtil.Table;
/**
 * Profundus Wrapper class for Item
 * 基本的にはItemにそのままメソッドを投げている。
 * @author kidocchy
 *
 */
public class PFItem implements Item{
	Item item;
	PFItem(Item i){
		item = i;
	}
	
	@Override
	public @NotNull Location getLocation() {
		return item.getLocation();
	}
	@Override
	public @Nullable Location getLocation(@Nullable Location loc) {
		return item.getLocation(loc);
	}
	@Override
	public void setVelocity(@NotNull Vector velocity) {
		item.setVelocity(velocity);
	}
	@Override
	public @NotNull Vector getVelocity() {
		return item.getVelocity();
	}
	@Override
	public double getHeight() {
		return item.getHeight();
	}
	@Override
	public double getWidth() {
		return item.getWidth();
	}
	@Override
	public @NotNull BoundingBox getBoundingBox() {
		return item.getBoundingBox();
	}
	@Override
	public boolean isOnGround() {
		return item.isOnGround();
	}
	@Override
	public boolean isInWater() {
		return item.isInWater();
	}
	@Override
	public @NotNull World getWorld() {
		return item.getWorld();
	}
	@Override
	public void setRotation(float yaw, float pitch) {
		item.setRotation(yaw, pitch);
	}
	@Override
	public boolean teleport(@NotNull Location location) {
		return item.teleport(location);
	}
	@Override
	public boolean teleport(@NotNull Location location, @NotNull TeleportCause cause) {
		return item.teleport(location, cause);
	}
	@Override
	public boolean teleport(@NotNull Entity destination) {
		return item.teleport(destination);
	}
	@Override
	public boolean teleport(@NotNull Entity destination, @NotNull TeleportCause cause) {
		return item.teleport(destination, cause);
	}
	@Override
	public @NotNull List<Entity> getNearbyEntities(double x, double y, double z) {
		return item.getNearbyEntities(x, y, z);
	}
	@Override
	public int getEntityId() {
		return item.getEntityId();
	}
	@Override
	public int getFireTicks() {
		return item.getFireTicks();
	}
	@Override
	public int getMaxFireTicks() {
		return item.getMaxFireTicks();
	}
	@Override
	public void setFireTicks(int ticks) {
		item.setFireTicks(ticks);
	}
	@Override
	public void setVisualFire(boolean fire) {
		item.setVisualFire(fire);
	}
	@Override
	public boolean isVisualFire() {
		return item.isVisualFire();
	}
	@Override
	public int getFreezeTicks() {
		return item.getFreezeTicks();
	}
	@Override
	public int getMaxFreezeTicks() {
		return item.getMaxFreezeTicks();
	}
	@Override
	public void setFreezeTicks(int ticks) {
		item.setFreezeTicks(ticks);
	}
	@Override
	public boolean isFrozen() {
		return item.isFrozen();
	}
	@Override
	public boolean isFreezeTickingLocked() {
		return item.isFreezeTickingLocked();
	}
	@Override
	public void lockFreezeTicks(boolean locked) {
		item.lockFreezeTicks(locked);
	}
	@Override
	public void remove() {
		item.remove();
	}
	@Override
	public boolean isDead() {
		return item.isDead();
	}
	@Override
	public boolean isValid() {
		return item.isValid();
	}
	@Override
	public @NotNull Server getServer() {
		return item.getServer();
	}
	@Override
	public boolean isPersistent() {
		return item.isPersistent();
	}
	@Override
	public void setPersistent(boolean persistent) {
		item.setPersistent(persistent);
	}
	@SuppressWarnings("deprecation")
	@Override
	public @Nullable Entity getPassenger() {
		return item.getPassenger();
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean setPassenger(@NotNull Entity passenger) {
		return item.setPassenger(passenger);
	}
	@Override
	public @NotNull List<Entity> getPassengers() {
		return item.getPassengers();
	}
	@Override
	public boolean addPassenger(@NotNull Entity passenger) {
		return item.addPassenger(passenger);
	}
	@Override
	public boolean removePassenger(@NotNull Entity passenger) {
		return item.removePassenger(passenger);
	}
	@Override
	public boolean isEmpty() {
		return item.isEmpty();
	}
	@Override
	public boolean eject() {
		return item.eject();
	}
	@Override
	public float getFallDistance() {
		return item.getFallDistance();
	}
	@Override
	public void setFallDistance(float distance) {
		item.setFallDistance(distance);
	}
	@Override
	public void setLastDamageCause(@Nullable EntityDamageEvent event) {
		item.setLastDamageCause(event);
	}
	@Override
	public @Nullable EntityDamageEvent getLastDamageCause() {
		return item.getLastDamageCause();
	}
	@Override
	public @NotNull UUID getUniqueId() {
		return item.getUniqueId();
	}
	@Override
	public int getTicksLived() {
		return item.getTicksLived();
	}
	@Override
	public void setTicksLived(int value) {
		item.setTicksLived(value);
	}
	@Override
	public void playEffect(@NotNull EntityEffect type) {
		item.playEffect(type);
	}
	@Override
	public @NotNull EntityType getType() {
		return item.getType();
	}
	@Override
	public boolean isInsideVehicle() {
		return item.isInsideVehicle();
	}
	@Override
	public boolean leaveVehicle() {
		return item.leaveVehicle();
	}
	@Override
	public @Nullable Entity getVehicle() {
		return item.getVehicle();
	}
	@Override
	public void setCustomNameVisible(boolean flag) {
		item.setCustomNameVisible(flag);
	}
	@Override
	public boolean isCustomNameVisible() {
		return item.isCustomNameVisible();
	}
	@Override
	public void setGlowing(boolean flag) {
		item.setGlowing(flag);
	}
	@Override
	public boolean isGlowing() {
		return item.isGlowing();
	}
	@Override
	public void setInvulnerable(boolean flag) {
		item.setInvulnerable(flag);
	}
	@Override
	public boolean isInvulnerable() {
		return item.isInvulnerable();
	}
	@Override
	public boolean isSilent() {
		return item.isSilent();
	}
	@Override
	public void setSilent(boolean flag) {
		item.setSilent(flag);
	}
	@Override
	public boolean hasGravity() {
		return item.hasGravity();
	}
	@Override
	public void setGravity(boolean gravity) {
		item.setGravity(gravity);
	}
	@Override
	public int getPortalCooldown() {
		return item.getPortalCooldown();
	}
	@Override
	public void setPortalCooldown(int cooldown) {
		item.setPortalCooldown(cooldown);
	}
	@Override
	public @NotNull Set<String> getScoreboardTags() {
		return item.getScoreboardTags();
	}
	@Override
	public boolean addScoreboardTag(@NotNull String tag) {
		return item.addScoreboardTag(tag);
	}
	@Override
	public boolean removeScoreboardTag(@NotNull String tag) {
		return item.removeScoreboardTag(tag);
	}
	@Override
	public @NotNull PistonMoveReaction getPistonMoveReaction() {
		return item.getPistonMoveReaction();
	}
	@Override
	public @NotNull BlockFace getFacing() {
		return item.getFacing();
	}
	@Override
	public @NotNull Pose getPose() {
		return item.getPose();
	}
	@Override
	public @NotNull SpawnCategory getSpawnCategory() {
		return item.getSpawnCategory();
	}
	@Override
	public @NotNull Spigot spigot() {
		return item.spigot();
	}
	@Override
	public @NotNull Component teamDisplayName() {
		return item.teamDisplayName();
	}
	@Override
	public @Nullable Location getOrigin() {
		return item.getOrigin();
	}
	@Override
	public boolean fromMobSpawner() {
		return item.fromMobSpawner();
	}
	@Override
	public @NotNull SpawnReason getEntitySpawnReason() {
		return item.getEntitySpawnReason();
	}
	@Override
	public boolean isInRain() {
		return item.isInRain();
	}
	@Override
	public boolean isInBubbleColumn() {
		return item.isInBubbleColumn();
	}
	@Override
	public boolean isInWaterOrRain() {
		return item.isInWaterOrRain();
	}
	@Override
	public boolean isInWaterOrBubbleColumn() {
		return item.isInWaterOrBubbleColumn();
	}
	@Override
	public boolean isInWaterOrRainOrBubbleColumn() {
		return item.isInWaterOrRainOrBubbleColumn();
	}
	@Override
	public boolean isInLava() {
		return item.isInLava();
	}
	@Override
	public boolean isTicking() {
		return item.isTicking();
	}
	@Override
	public @NotNull Set<Player> getTrackedPlayers() {
		return item.getTrackedPlayers();
	}
	@Override
	public boolean spawnAt(@NotNull Location location, @NotNull SpawnReason reason) {
		return item.spawnAt(location, reason);
	}
	@Override
	public boolean isInPowderedSnow() {
		return item.isInPowderedSnow();
	}
	@Override
	public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
		item.setMetadata(metadataKey, newMetadataValue);
	}
	@Override
	public @NotNull List<MetadataValue> getMetadata(@NotNull String metadataKey) {
		return item.getMetadata(metadataKey);
	}
	@Override
	public boolean hasMetadata(@NotNull String metadataKey) {
		return item.hasMetadata(metadataKey);
	}
	@Override
	public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
		item.removeMetadata(metadataKey, owningPlugin);
	}
	@Override
	public void sendMessage(@NotNull String message) {
		item.sendMessage(message);
	}
	@Override
	public void sendMessage(@NotNull String... messages) {
		item.sendMessage(messages);
	}
	@Override
	public void sendMessage(@Nullable UUID sender, @NotNull String message) {
		item.sendMessage(sender, message);
	}
	@Override
	public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
		item.sendMessage(sender, messages);
	}
	@Override
	public @NotNull String getName() {
		return item.getName();
	}
	@Override
	public @NotNull Component name() {
		return item.name();
	}
	@Override
	public boolean isPermissionSet(@NotNull String name) {
		return item.isPermissionSet(name);
	}
	@Override
	public boolean isPermissionSet(@NotNull Permission perm) {
		return item.isPermissionSet(perm);
	}
	@Override
	public boolean hasPermission(@NotNull String name) {
		return item.hasPermission(name);
	}
	@Override
	public boolean hasPermission(@NotNull Permission perm) {
		return item.hasPermission(perm);
	}
	@Override
	public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
		return item.addAttachment(plugin, name, value);
	}
	@Override
	public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
		return item.addAttachment(plugin);
	}
	@Override
	public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value,
			int ticks) {
		return item.addAttachment(plugin, name, value, ticks);
	}
	@Override
	public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
		return item.addAttachment(plugin, ticks);
	}
	@Override
	public void removeAttachment(@NotNull PermissionAttachment attachment) {
		item.removeAttachment(attachment);
	}
	@Override
	public void recalculatePermissions() {
		item.recalculatePermissions();
	}
	@Override
	public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return item.getEffectivePermissions();
	}
	@Override
	public boolean isOp() {
		return item.isOp();
	}
	@Override
	public void setOp(boolean value) {
		item.setOp(value);
	}
	@Override
	public @Nullable Component customName() {
		return item.customName();
	}
	@Override
	public void customName(@Nullable Component customName) {
		item.customName(customName);
	}
	@SuppressWarnings("deprecation")
	@Override
	public @Nullable String getCustomName() {
		return item.getCustomName();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void setCustomName(@Nullable String name) {
		item.setCustomName(name);
	}
	@Override
	public @NotNull PersistentDataContainer getPersistentDataContainer() {
		return item.getPersistentDataContainer();
	}
	@Override
	public @NotNull ItemStack getItemStack() {
		return item.getItemStack();
	}
	@Override
	public void setItemStack(@NotNull ItemStack stack) {
		item.setItemStack(stack);
	}
	@Override
	public int getPickupDelay() {
		return item.getPickupDelay();
	}
	@Override
	public void setPickupDelay(int delay) {
		item.setPickupDelay(delay);
	}
	@Override
	public void setOwner(@Nullable UUID owner) {
		item.setOwner(owner);
		//TODO ここで管理台帳に追加，
	}
	public void resetOwner(@NotNull UUID pfid) {
		//TODO
	}
	@Override
	public @Nullable UUID getOwner() {
		return item.getOwner();
	}
	public Boolean checkOwnerInfo() {
		//TODO 管理台帳と整合性検証
		//IF (!MATCHED) {setOwner(null);}
		//管理台帳にログ追加
		return false;
	}
	@Override
	public void setThrower(@Nullable UUID uuid) {
		item.setThrower(uuid);
	}
	@Override
	public @Nullable UUID getThrower() {
		return item.getThrower();
	}
	@Override
	public boolean canMobPickup() {
		return item.canMobPickup();
	}
	@Override
	public void setCanMobPickup(boolean canMobPickup) {
		item.setCanMobPickup(canMobPickup);
	}
	@Override
	public boolean canPlayerPickup() {
		return item.canPlayerPickup();
	}
	@Override
	public void setCanPlayerPickup(boolean canPlayerPickup) {
		item.setCanPlayerPickup(canPlayerPickup);
	}
	@Override
	public boolean willAge() {
		return item.willAge();
	}
	@Override
	public void setWillAge(boolean willAge) {
		item.setWillAge(willAge);
	}
	@Override
	public int getHealth() {
		return item.getHealth();
	}
	@Override
	public void setHealth(int health) {
		item.setHealth(health);
	}
	
}
