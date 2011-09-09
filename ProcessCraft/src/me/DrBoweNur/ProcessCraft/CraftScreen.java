package me.DrBoweNur.ProcessCraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class CraftScreen {
	private Location loc1, loc2;
	private World world;
	private double width, height, constantCoor, xOrigin, yOrigin;
	private ScreenType type;
	private boolean isVisible;
	private Material screenMat;
	
	/**
	 * Types of screens
	 * @XY
	 * 	Across X | Up Y
	 * @ZY
	 * 	Across Z | Up Y
	 * @XZ	
	 * 	Across X | Up Z (Flat screen, no pun intended)
	 */
	public enum ScreenType{
		XY,
		ZY,
		XZ,
		ZX;
	}
	
	/**
	 * Creates a new CraftScreen
	 * @param loc
	 * 	starting location
	 * @param width
	 * 	screen width
	 * @param height
	 * 	screen height
	 * @param screenType
	 * 	screen type (XY,ZY,XZ)
	 */
	public CraftScreen(Location loc, double width, double height, ScreenType screenType, Material m){
		this.isVisible = false;
		this.type = screenType;
		this.setWorld(loc.getWorld());
		this.loc1 = loc;
		this.width = width;
		this.height = height;
		this.xOrigin = getXOrigin();
		this.yOrigin = getYOrigin();
		this.screenMat = m;
		
		double x,y,z;
			x = loc.getX();
			y = loc.getY();
			z = loc.getZ();
		
		switch(screenType){
		case XY:
			loc2 = new Location(loc.getWorld(), x+width, y+height, constantCoor(z));
			break;
		case ZY:
			loc2 = new Location(loc.getWorld(), constantCoor(x), y+height, z+width);
			break;
		case XZ:
			loc2 = new Location(loc.getWorld(), x+width, constantCoor(y), z+height);
		case ZX:
			loc2 = new Location(loc.getWorld(), x+height, constantCoor(y), z+width);
		}
		
	}
	
	/**
	 * Displays the screen
	 */
	public void display(){
		for(double x = 0; x <= width; x++){
			for(double y = 0; y <= height; y++){
				Block b = world.getBlockAt(getScreenCoordinate(x,y));
				b.setType(screenMat);
			}
		}
	}
	
	//Might be deprecated
	public void display(Material m){
		for(double x = 0; x <= width; x++){
			for(double y = 0; y <= height; y++){
				Block b = world.getBlockAt(getScreenCoordinate(x,y));
				b.setType(m);
			}
		}
	}
	
	
	private double constantCoor(double coor){
		this.constantCoor = coor;
		return coor;
	}
	
	/**
	 * Calculates and returns a Bukkit Location of the given screen coordinates,
	 * keeping in mind the possible screen orientations
	 * @param x
	 * 	x-coordinate (of the screen)
	 * @param y
	 *  y-coordinate (of the screen)
	 * @return
	 * 	Location for use in CraftApplet
	 */
	public Location getScreenCoordinate(double x, double y){
		switch(type){
		case XY:
			return new Location(world, xOrigin+x, yOrigin+y, constantCoor);
		case ZY:
			return new Location(world, constantCoor, yOrigin+y, xOrigin+x);
		case XZ:
			return new Location(world, xOrigin+y, constantCoor, yOrigin+x);
		case ZX:
			return new Location(world, xOrigin+x, constantCoor, yOrigin+y);
		}
		return null;
	}
	
	public boolean isPointOnScreen(Location toCheck){
		double x1,y1,z1,x2,y2,z2,x,y,z;
		x1 = loc1.getX();
		y1 = loc1.getY();
		z1 = loc1.getZ();
		x2 = loc2.getX();
		y2 = loc2.getY();
		z2 = loc2.getZ();
		x = toCheck.getX();
		y = toCheck.getY();
		z = toCheck.getZ();
		switch(type){
		case XY:
			if(x < x1 || x > x2 || y < y1 || y > y2)
				return false;
		case ZY:
			if(z < z1 || z > z2 || y < y1 || y > y2)
				return false;
		case XZ:
			if(x < x1 || x > x2 || z < z1 || z > z2)
				return false;
		case ZX:
			if(z < z1 || z > z2 || x < x1 || x > x2)
				return false;
		}
		return true;
	}
	
	/**
	 * Gets the screen type
	 * @return
	 * 	screen type (XY, ZY, XZ)
	 */
	public ScreenType getType(){
		return this.type;
	}
	
	/**
	 * Gets the xOrigin for this screen, based on the screen type
	 * @return
	 * 	xOrigin (-1 if error)
	 */
	public double getXOrigin(){
		switch(this.type){
			case XY:
				return this.loc1.getX();
			case ZY:
				return this.loc1.getZ();
			case XZ:
				return this.loc1.getX();
			case ZX:
				return this.loc1.getX();
		}
		return -1;
	}
	
	/**
	 * Gets the yOrigin for this screen, based on the screen type
	 * @return
	 * 	yOrigin (-1 if error)
	 */
	public double getYOrigin(){
		switch(this.type){
			case XY:
				return this.loc1.getY();
			case ZY:
				return this.loc1.getY();
			case XZ:
				return this.loc1.getZ();
			case ZX:
				return this.loc1.getX();
		}
		return -1;
	}
	
	/**
	 * Gets the screen height
	 * @return
	 * 	screen height
	 */
	public double getHeight(){
		return this.height;
	}
	
	/**
	 * Gets the screen width
	 * @return
	 * 	screen width
	 */
	public double getWidth(){
		return this.width;
	}
	
	public void setVisible(boolean state){
		isVisible = state;
	}
	
	public boolean isVisible(){
		return this.isVisible;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}
	

}
