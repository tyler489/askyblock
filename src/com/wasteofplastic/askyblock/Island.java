package com.wasteofplastic.askyblock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

/**
 * Stores all the info about an island
 * Managed by GridManager
 * @author tastybento
 *
 */
public class Island {

    // Coordinates of the island area
    private int minX;
    private int minZ;
    // Coordinates of minimum protected area
    private int minProtectedX;
    private int minProtectedZ;
    // Protection size
    private int protectionRange;
    // Height of island
    private int y;
    // The actual center of the island itself
    private Location center;
    // World the island is in
    private World world;
    // The owner of the island
    private UUID owner;
    // Time parameters
    private long createdDate;
    private long updatedDate;
    // A password associated with the island
    private String password;
    // Votes for how awesome the island is
    private int votes;
    private int islandDistance;
    private boolean locked = false;
    // Set if this island is a spawn island
    private boolean isSpawn = false;
    // Stats variables
    private HashMap<EntityType,Integer> entities = new HashMap<EntityType,Integer>();

    protected Island(String serial) {
	//Bukkit.getLogger().info("DEBUG: adding serialized island to grid ");
	// Deserialize
	// Format:
	// x:height:z:protection range:island distance:owner UUID
	String[] split = serial.split(":");
	try {
	    protectionRange = Integer.parseInt(split[3]);
	    islandDistance = Integer.parseInt(split[4]);
	    int x = Integer.parseInt(split[0]);
	    int z = Integer.parseInt(split[2]);
	    minX = x - islandDistance/2;
	    y = Integer.parseInt(split[1]);
	    minZ = z - islandDistance/2;
	    minProtectedX = x - protectionRange/2;
	    minProtectedZ = z - protectionRange/2;  
	    this.world = ASkyBlock.getIslandWorld();
	    this.center = new Location(world,x,y,z);
	    this.createdDate = new Date().getTime();
	    this.updatedDate = createdDate;
	    this.password = "";
	    this.votes = 0;
	    if (split.length> 6) {
		//Bukkit.getLogger().info("DEBUG: " + split[6]);
		// Get locked status
		if (split[6].equalsIgnoreCase("true")) {
		    this.locked = true;
		} else {
		    this.locked = false;
		}
		//Bukkit.getLogger().info("DEBUG: " + locked);
	    } else {
		this.locked = false;
	    }
	    if (!split[5].equals("null")) {
		if (split[5].equals("spawn")) {
		    isSpawn = true;
		} else {
		    owner = UUID.fromString(split[5]);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Add a new island using the island center method
     * @param minX
     * @param minZ
     */
    protected Island(int x, int z) {
	// Calculate min minX and z
	this.minX = x - Settings.islandDistance/2;
	this.minZ = z - Settings.islandDistance/2;
	this.minProtectedX = x - Settings.island_protectionRange/2;
	this.minProtectedZ = z - Settings.island_protectionRange/2;
	this.y = Settings.island_level;
	this.islandDistance = Settings.islandDistance;
	this.protectionRange = Settings.island_protectionRange;
	this.world = ASkyBlock.getIslandWorld();
	this.center = new Location(world,x,y,z);
	this.createdDate = new Date().getTime();
	this.updatedDate = createdDate;
	this.password = "";
	this.votes = 0;
    }

    protected Island(int x, int z, UUID owner) {
	// Calculate min minX and z
	this.minX = x - Settings.islandDistance/2;
	this.minZ = z - Settings.islandDistance/2;
	this.minProtectedX = x - Settings.island_protectionRange/2;
	this.minProtectedZ = z - Settings.island_protectionRange/2;
	this.y = Settings.island_level;
	this.islandDistance = Settings.islandDistance;
	this.protectionRange = Settings.island_protectionRange;
	this.world = ASkyBlock.getIslandWorld();
	this.center = new Location(world,x,y,z);
	this.createdDate = new Date().getTime();
	this.updatedDate = createdDate;
	this.password = "";
	this.votes = 0;
	this.owner = owner;
    }
    /**
     * @param minX
     * @param z
     * @param protectionRange
     * @param center
     * @param owner
     * @param createdDate
     * @param updatedDate
     * @param password
     * @param votes
     */
    protected Island(int x, int z, int protectionRange, Location center, UUID owner, long createdDate, long updatedDate, String password, int votes) {
	this.minX = x - Settings.islandDistance/2;
	this.minZ = z - Settings.islandDistance/2;
	this.minProtectedX = x - Settings.island_protectionRange/2;
	this.minProtectedZ = z - Settings.island_protectionRange/2;
	this.protectionRange = protectionRange;
	this.center = center;
	this.world = center.getWorld();
	this.y = center.getBlockY();
	this.owner = owner;
	this.createdDate = createdDate;
	this.updatedDate = updatedDate;
	this.password = password;
	this.votes = votes;
    }

    /**
     * Checks if a location is within this island's protected area
     * @param loc
     * @return
     */
    protected boolean onIsland(Location target) {
	if (target.getWorld().equals(world)) {
	    //Bukkit.getLogger().info("Target = " + target.getBlockX() + "," + target.getBlockZ());
	    //Bukkit.getLogger().info("Center = " + center.getBlockX() + "," + center.getBlockZ());
	    //Bukkit.getLogger().info(target.getX() + ">=" + (center.getBlockX() - protectionRange / 2));
	    //Bukkit.getLogger().info(target.getX() + "<" + (center.getBlockX() + protectionRange / 2));
	    //Bukkit.getLogger().info(target.getZ() + ">=" + (center.getBlockZ() - protectionRange / 2));
	    //Bukkit.getLogger().info(target.getZ() + "<" + (center.getBlockZ() + protectionRange / 2));
	    if (target.getX() >= center.getBlockX() - protectionRange / 2
		    && target.getX() < center.getBlockX() + protectionRange / 2
		    && target.getZ() >= center.getBlockZ() - protectionRange / 2
		    && target.getZ() < center.getBlockZ() + protectionRange / 2) {
		return true;
	    }
	}
	return false;	
    }

    /**
     * Checks if location is anywhere in the island space (island distance)
     * @param target
     * @return true if in the area
     */
    protected boolean inIslandSpace(Location target) {
	/*
	    Bukkit.getLogger().info("Target = " + target.getBlockX() + "," + target.getBlockZ());
	    Bukkit.getLogger().info("Center = " + center.getBlockX() + "," + center.getBlockZ());
	    Bukkit.getLogger().info(target.getX() + ">=" + (center.getBlockX() - islandDistance / 2));
	    Bukkit.getLogger().info(target.getX() + "<" + (center.getBlockX() + islandDistance / 2));
	    Bukkit.getLogger().info(target.getZ() + ">=" + (center.getBlockZ() - islandDistance / 2));
	    Bukkit.getLogger().info(target.getZ() + "<" + (center.getBlockZ() + islandDistance / 2));
	 */
	if (target.getWorld().equals(world)) {
	    if (target.getX() >= center.getBlockX() - islandDistance / 2
		    && target.getX() < center.getBlockX() + islandDistance / 2
		    && target.getZ() >= center.getBlockZ() - islandDistance / 2
		    && target.getZ() < center.getBlockZ() + islandDistance / 2) {
		return true;
	    }
	}
	return false;	
    }

    protected boolean inIslandSpace(int x, int z) {
	if (x >= center.getBlockX() - islandDistance / 2
		&& x < center.getBlockX() + islandDistance / 2
		&& z >= center.getBlockZ() - islandDistance / 2
		&& z < center.getBlockZ() + islandDistance / 2) {
	    return true;
	}
	return false;	
    }
    /**
     * @return the minX
     */
    protected int getMinX() {
	return minX;
    }
    /**
     * @param minX the minX to set
     */
    protected void setMinX(int minX) {
	this.minX = minX;
    }
    /**
     * @return the z
     */
    protected int getMinZ() {
	return minZ;
    }
    /**
     * @param z the z to set
     */
    protected void setMinZ(int minZ) {
	this.minZ = minZ;
    }
    /**
     * @return the minProtectedX
     */
    protected int getMinProtectedX() {
	return minProtectedX;
    }

    /**
     * @return the minProtectedZ
     */
    protected int getMinProtectedZ() {
	return minProtectedZ;
    }

    /**
     * @return the protectionRange
     */
    protected int getProtectionSize() {
	return protectionRange;
    }
    /**
     * @param protectionRange the protectionRange to set
     */
    protected void setProtectionSize(int protectionSize) {
	this.protectionRange = protectionSize;
	this.minProtectedX = center.getBlockX() - protectionSize/2;
	this.minProtectedZ = center.getBlockZ() - protectionSize/2;

    }
    /**
     * @return the islandDistance
     */
    protected int getIslandDistance() {
	return islandDistance;
    }

    /**
     * @param islandDistance the islandDistance to set
     */
    protected void setIslandDistance(int islandDistance) {
	this.islandDistance = islandDistance;
    }

    /**
     * @return the center
     */
    protected Location getCenter() {
	return center;
    }
    /**
     * @param center the center to set
     */
    protected void setCenter(Location center) {
	this.center = center;
    }
    /**
     * @return the owner
     */
    protected UUID getOwner() {
	return owner;
    }
    /**
     * @param owner the owner to set
     */
    protected void setOwner(UUID owner) {
	this.owner = owner;
    }
    /**
     * @return the createdDate
     */
    protected long getCreatedDate() {
	return createdDate;
    }
    /**
     * @param createdDate the createdDate to set
     */
    protected void setCreatedDate(long createdDate) {
	this.createdDate = createdDate;
    }
    /**
     * @return the updatedDate
     */
    protected long getUpdatedDate() {
	return updatedDate;
    }
    /**
     * @param updatedDate the updatedDate to set
     */
    protected void setUpdatedDate(long updatedDate) {
	this.updatedDate = updatedDate;
    }
    /**
     * @return the password
     */
    protected String getPassword() {
	return password;
    }
    /**
     * @param password the password to set
     */
    protected void setPassword(String password) {
	this.password = password;
    }
    /**
     * @return the votes
     */
    protected int getVotes() {
	return votes;
    }
    /**
     * @param votes the votes to set
     */
    protected void setVotes(int votes) {
	this.votes = votes;
    }

    /**
     * @return the locked
     */
    protected boolean isLocked() {
	return locked;
    }

    /**
     * @param locked the locked to set
     */
    protected void setLocked(boolean locked) {
	//Bukkit.getLogger().info("DEBUG: island is now " + locked);
	this.locked = locked;
    }

    protected String serialize() {
	// x:height:z:protection range:island distance:owner UUID
	String ownerString = "null";
	if (owner != null) {
	    ownerString = owner.toString();
	}
	if (isSpawn) {
	    //Bukkit.getLogger().info("DEBUG: island is spawn");
	    ownerString = "spawn";
	}
	return center.getBlockX() + ":" + center.getBlockY() + ":" + center.getBlockZ() + ":" + protectionRange 
		+ ":" + islandDistance + ":" + ownerString + ":" + locked;
    }

    /**
     * Provides a list of all the players who are allowed on this island
     * including coop members
     * @return a list of UUIDs that have legitimate access to the island
     */
    protected List<UUID> getMembers() {
	List<UUID> result = new ArrayList<UUID>();
	// Add any coop members for this island
	result.addAll(CoopPlay.getInstance().getCoopPlayers(center));
	if (owner == null) {
	    return result;
	}
	result.add(owner);
	// Add any team members
	result.addAll(ASkyBlock.getPlugin().getPlayers().getMembers(owner));
	return result;
    }

    /**
     * @return the isSpawn
     */
    protected boolean isSpawn() {
	return isSpawn;
    }

    /**
     * @param isSpawn the isSpawn to set
     */
    protected void setSpawn(boolean isSpawn) {
	this.isSpawn = isSpawn;
    }

    protected void addEntity(EntityType type) {
	if (this.entities.containsKey(type)) {
	    int sum = this.entities.get(type);
	    this.entities.put(type, (sum + 1));
	} else {
	    this.entities.put(type, 1);
	}
    }

    protected int getEntity(EntityType type) {
	if (this.entities.containsKey(type)) {
	    return this.entities.get(type);
	} 
	return 0;
    }
        
    /**
     * @return the entities
     */
    protected HashMap<EntityType, Integer> getEntities() {
        return entities;
    }

    protected void clearStats() {
	this.entities.clear();
    }
}