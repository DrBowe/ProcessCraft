package me.DrBoweNur.ProcessCraft;

//import org.bukkit.Server;
import me.DrBoweNur.ProcessCraft.CraftScreen.ScreenType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CraftApplet {
	public double WIDTH, HEIGHT;
	private RectMode rectMode;
	protected JavaPlugin plugin;
	private int animationID, calculationID;
	private CraftScreen screen;
	private World world;

	public CraftApplet(JavaPlugin instance){
		this.plugin = instance;
		this.rectMode = RectMode.CORNER;
	}

	/**
	 * Starts the program at 20FPS
	 */
	public void run(){
		/*if(!screen.isVisible())
			return;
		screen.display();*/
		setup();
		calculationID = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				calculate();
			}
		}, 0, 1);
		animationID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				draw();
			}
		}, 0, 1);
	}

	/**
	 * Starts the program at given FPS
	 * @param frameRate
	 * 	Frames per second (max is 20)
	 */
	public void run(int frameRate){
		/*if(!screen.isVisible())
			return;
		screen.display();*/
		setup();
		calculationID = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				calculate();
			}
		}, 0, (20/frameRate));
		animationID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				draw();
			}
		}, 0, (20/frameRate));
	}

	/**
	 * Ends the program and closes the screen
	 */
	public void exit(){
		plugin.getServer().getScheduler().cancelTask(animationID);
		plugin.getServer().getScheduler().cancelTask(calculationID);
		//screen.remove();
	}

	/**
	 * Links a CraftScreen with the current Applet
	 * @param screen
	 * 	screen to link
	 */
	public void attachScreen(CraftScreen screen){
		this.screen = screen;
		//this.xOrigin = screen.getXOrigin();
		//this.yOrigin = screen.getYOrigin();
		this.HEIGHT = screen.getHeight();
		this.WIDTH = screen.getWidth();
		this.world = screen.getWorld();
	}

	/**
	 * Links a NEW CraftScreen with the current Applet
	 * @param loc
	 * 	starting location of the screen
	 * @param width
	 * 	width of the screen
	 * @param height
	 * 	height of the screen
	 * @param screenType
	 * 	Type of the screen (XY, ZY, XZ)
	 */
	public void attachScreen(Location loc, double width, double height, ScreenType screenType, Material m){
		this.screen = new CraftScreen(loc, width, height, screenType, m);
		//this.xOrigin = screen.getXOrigin();
		//this.yOrigin = screen.getYOrigin();
		this.HEIGHT = screen.getHeight();
		this.WIDTH = screen.getWidth();
		this.world = screen.getWorld();
	}

	/**
	 * Self-explanatory
	 * @return
	 * 	attached screen
	 */
	public CraftScreen getAttachedScreen(){
		return screen;
	}

	/**
	 * Method that is called one time before a program
	 * starts up (only draw is called repeatedly)
	 */
	protected abstract void setup();

	/**
	 * Method that runs in a separate thread that -should- be used
	 * to calculate things that will occur in draw. This method is
	 * provided to reduce stress on the server. Do NOT place any 
	 * Bukkit code here, as most of it is not thread safe.
	 */
	protected abstract void calculate();

	/**
	 * Method that will be called upon repeatedly while the
	 * program is running.
	 */
	protected abstract void draw();

	/**
	 * Draws a pixel on the screen at (x,y)
	 * @param x
	 * 	x Coordinate
	 * @param y
	 * 	y Coordinate
	 * @param m
	 * 	Minecraft material of the pixel
	 */
	protected void point(double xCoor, double yCoor, Material m){
		//double x = xOrigin+xCoor;
		//double y = yOrigin+yCoor;
		Location point = screen.getScreenCoordinate(xCoor, yCoor);
		if(!screen.isPointOnScreen(point))
			return;
		Block b = world.getBlockAt(point);
		b.setType(m);
	}

	/**
	 * Sets the current RectMode, used for defining the rect() method
	 * @param mode
	 * 	mode to set it to
	 */
	public void rectMode(RectMode mode){
		this.rectMode = mode;
	}

	/**
	 * Draws a rectangle within from (x,y) with width w and height h
	 * @param x
	 * 	x Coordinate
	 * @param y
	 * 	y Coordinate
	 * @param width
	 *  rectangle width
	 * @param height
	 * 	rectangle height
	 * @param m
	 * 	Minecraft material to fill in with
	 */
	protected void rect(double xVar1, double yVar1, double xVar2, double yVar2, Material m){
		switch(this.rectMode){
		// (@xVar1,@yVar1) = bottom-left corner of rectangle
		// @xVar2 = width, @yVar2 = height
		case CORNER:
			for(double x = xVar1; x < xVar1+xVar2; x++){
				for(double y = yVar1; y < yVar1+yVar2; y++){
					Block b = world.getBlockAt(screen.getScreenCoordinate(x, y));
					b.setType(m);
				}
			}
			break;
			// (@xVar1,@yVar1) = bottom-left corner of rectangle
			// (@xVar2,@yVar2) = top-right corner of rectangle	
		case CORNERS:
			for(double x = xVar1; x <= xVar2; x++){
				for(double y = yVar1; y <= yVar2; y++){
					Block b = world.getBlockAt(screen.getScreenCoordinate(x,y));
					b.setType(m);
				}
			}
			break;
			// (@xVar1,@yVar1) = center point of rectangle
			// @xVar2 = width, @yVar2 = height
		case CENTER:
			for(double x = (xVar1-(int)(xVar2/2)); x < xVar1+xVar2; x++){
				for(double y = (yVar1-(int)(yVar2/2)); y < yVar1+yVar2; y++){
					Block b = world.getBlockAt(screen.getScreenCoordinate(x,y));
					b.setType(m);
				}
			}
			break;
		}
	}

	protected void line(double xCoor1, double yCoor1, double xCoor2, double yCoor2, Material m){
		double slope,maxX,minX;
		maxX = Math.max(xCoor1, xCoor2);
		minX = Math.min(xCoor1, xCoor2);
		double[] yValues = new double[(int) Math.abs(xCoor2-xCoor1)];
		//handle undefined slopes
		if(xCoor2-xCoor1 == 0){
			for(double y = Math.min(yCoor1, yCoor2); y <= Math.max(yCoor1,yCoor2); y++){
				point(xCoor1, y, m);
			}
			return;
		}
		
		slope = (yCoor2-yCoor1)/(xCoor2-xCoor1);
		//Calculate the y-values and store them in the array
		for(double x = 0; x < Math.abs(xCoor2-xCoor1); x++){
			double y;
			if(minX == xCoor1){
				y = (slope*x)+ yCoor1;
			}
			else{
				y = (slope*x)+ yCoor2;
			}
			yValues[(int)x] = y;
		}
		//load y-values out of the array and place them with
		//their corresponding x-values
		for(double x = minX; x < maxX; x++){
			point(x, yValues[(int)(x-minX)], m);
		}
	}


protected void triangle(double xCoor1, double yCoor1, double xCoor2, double yCoor2, double xCoor3, double yCoor3, Material m){
	line(xCoor1, yCoor1, xCoor2, yCoor2, m);
	line(xCoor2, yCoor2, xCoor3, yCoor3, m);
	line(xCoor3, yCoor3, xCoor1, yCoor1, m);
}

//TODO: Make function for determining increment
protected void ellipse(double xCoor, double yCoor, double width, double height, Material m){
	double a = width/2;
	double b = height/2;
	double b2 = Math.pow(b, 2);
	double a2 = Math.pow(a, 2);
	double inc = (Math.sqrt(a2 + b2/2))*2.5;
	double x,y,r;
	for (double i = 0; i < 2*Math.PI; i += 1/inc ){
		r = (b2)/(a*(Math.cos(i) * Math.sqrt((1- (b2/Math.pow(a, 2)))) +1));
		x = r*Math.cos(i);
		y = r*Math.sin(i);
		point(Math.floor(x+xCoor+a-1),Math.floor(y+yCoor),m);
	}
}

protected void background(Material m){
	screen.display(m);
}

public void displayScreen(){
	screen.display(Material.WOOL);
}
}
