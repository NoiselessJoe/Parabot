package PKHonorSmithing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.Loader;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.SceneObject;
import org.rev317.min.api.wrappers.Tile;


@ScriptManifest
(author = "NoiselessJoe", 
category = Category.MINING, 
description = "Mines clay, turns it into soft clay, then banks.", 
name = "nSoftClay", 
servers = { "PKHonor" }, 
version = 1)

public class nSoftClay extends Script implements Paintable
{

	private final ArrayList<Strategy> strategies = new ArrayList<>();
	
	private Timer timer = new Timer();
	private int softClay = 1762;
	private int clay = 435;
	private int miningAnim = 625;
	public int clayMade = 0;
	public boolean swag = false;
	private int[] randoms = { 410, 1091, 3117, 3022, 3351, 409 };
	private final Color color1 = new Color(255, 255, 255); /*White*/
    private final Font font1 = new Font("Verdana", 1, 11);/*Bold, Sized 11 Verdana Font*/
	private Image img1;
    private int rCount;
    private final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
    Area bobsIsland = new Area(new Tile(2511, 4765),
    new Tile(2511, 4790),
    new Tile(2542, 4790),
    new Tile(2542, 4765));
    
    
    public boolean onExecute()
	{
		img1 = getImage("http://i.imgur.com/koOqd7N.png");
    	strategies.add(new makeClay());
		strategies.add(new Banking());
		strategies.add(new Mine());
			
		provide(strategies);
		return true;
	}
/**************************************************************************/
	public class Mine implements Strategy
	{
	
		@Override
		public boolean activate() 
		{		
			return (Players.getMyPlayer().getAnimation() == -1 
			|| Players.getMyPlayer().getAnimation() == 1353) 
			&& Inventory.getCount(softClay) == 0
			&& !bobsIsland.contains(Players.getMyPlayer().getLocation());
		}
	
		@Override
		public void execute() 
		{
			final SceneObject clayOres[] = SceneObjects.getNearest(11191,11190,11184,11189);
			if (clayOres.length > 0)
			{
				final SceneObject clayRock = clayOres[0];
				if(clayRock != null)
				{
					clayRock.interact(0);
					Time.sleep(new SleepCondition() 
					{
						
						@Override
						public boolean isValid() 
						{							
							return Players.getMyPlayer().getAnimation() == miningAnim;
						}
					}, 4000);				
				}	
			}
		}		
	}	
/**************************************************************************************/
	public class makeClay implements Strategy
	{

		@Override
		public boolean activate() 
		{	
			return Inventory.getCount(clay) > 0
			&& Inventory.getCount(softClay) >= 0
			&& Inventory.isFull()
			&& !bobsIsland.contains(Players.getMyPlayer().getLocation());
		}

		@Override
		public void execute() 
		{
			swag = true;
			final SceneObject Sinks[] = SceneObjects.getNearest(9684);	
			if(Sinks.length > 0)
			{
				final SceneObject sink = Sinks[0];
				if(sink != null)
				{
					Menu.sendAction(447, clay - 1, Inventory.getItems(clay)[0].getSlot(), 3214);
					Time.sleep(300);
					Menu.sendAction(62, sink.getHash(), sink.getLocalRegionX(), sink.getLocalRegionY());
					
					Time.sleep(new SleepCondition() 
					{
					
					@Override
						public boolean isValid() 
						{
						
						return Inventory.getCount(clay) == 0;
						}
					}, 26000);	
				}
			}						
		}
	}
/**************************************************************************************************************/
	public class Banking implements Strategy
	{

		@Override
		public boolean activate()
		{
			return Inventory.isFull()
			&& Inventory.getCount(softClay) > 0
			&& Inventory.getCount(clay) == 0
			&& !bobsIsland.contains(Players.getMyPlayer().getLocation());
		}
	
		@Override
		public void execute() 
		{
			final SceneObject[] Banks = SceneObjects.getNearest(2213);
			if(Banks.length > 0)
			{
				final SceneObject booth = Banks[0];
				if(booth != null)
				{
					booth.interact(0);
					Time.sleep(new SleepCondition() 
					{		
						@Override
						public boolean isValid() 
						{
							/*Check if bank interface is open*/
							return Loader.getClient().getOpenInterfaceId() == 23350;
						}
					}, 4000);
					
					/*Deposists Everything into bank*/
					if(Loader.getClient().getOpenInterfaceId() == 23350)
					{
						clayMade = clayMade + Inventory.getCount(softClay);
						Time.sleep(500);
						Menu.clickButton(23412);
					
						Time.sleep(new SleepCondition() 
						{
						
							@Override
							public boolean isValid() 
							{
							
								/*When inventory is empty, it will withdrawl gold ore.*/
								return Inventory.isEmpty();
							}
						}, 3000);
					}
				}
			swag = false;
			}
		}
		
	}
/****************************************************************************************************/
	//Thanks to Minimal for the Anti-Randoms
	//http://www.parabot.org/community/user/10775-minimal/
		public class Antis implements Strategy
	{
	    public boolean activate()
	    {
	        for (Npc n : Npcs.getNearest(randoms))
	        {
	            if (n.getLocation().distanceTo() < 3)
	                return true;
	        }

	        return false;
	    }

	    public void execute()
	    {
	        if (runnable != null)
	            runnable.run();

	        sleep(500);

	        Npc[] n = Npcs.getNearest(randoms);

	        System.out.println("There is a random nearby!");

	        sleep(500);

	        try
	        {
	            if (n[0].getDef().getId() == 1091 
	            && bobsIsland.contains(Players.getMyPlayer().getLocation()))
	            {
	                // Bob anti-random

	                SceneObject[] portal = SceneObjects.getNearest(8987);

	                for (int i = 0; i < portal.length; i++)
	                {
	                    if (bobsIsland.contains(Players.getMyPlayer().getLocation()))
	                    {
	                        final SceneObject portal2 = portal[i];

	                        portal2.interact(0);

	                        Time.sleep(new SleepCondition()
	                        {
	                            @Override
	                            public boolean isValid()
	                            {
	                                return portal2.getLocation().distanceTo() < 1;
	                            }

	                        }, 7500);

	                        sleep(1000);
	                    }
	                    else
	                        break;
	                }

	                System.out.println("Bob's Island has been completed");
	            }
	            else if (n[0].getDef().getId() == 3022)
	            {
	                System.out.println("A mod called a Genie random.\n" +
	                        "The client was closed to protect your account.");

	                System.exit(0);
	            }
	            else
	            {
	                n[0].interact(0);
	                sleep(2000);

	                System.out.println("Sandwich lady/Old man random has been completed");
	            }
	        }
	        catch (NullPointerException | ArrayIndexOutOfBoundsException e)
	        {
	            System.out.println(e.getMessage());
	            sleep(1500);
	        }

	        rCount++;
	    }
	}
/*****************************************************************************************************/
	/*Formats numbers, so 1000000 will output as 1,000,000*/
	public String addDecimals(int item)
	{
	    DecimalFormat form = new DecimalFormat("#,###");
	    return "" + form.format(item);
	}
	//Method to get Image from online (ie. Imgur)
	public static Image getImage(String url)
	{
		try {
			
			return ImageIO.read(new URL(url));
		} catch (IOException e) 
		{
			return null;
		}
	}
	@Override
	public void paint(Graphics arg0)
	{
		
		int xpos = 545;
		int ypos = 347;
		Graphics2D g = (Graphics2D) arg0;
		g.drawImage(img1,xpos, ypos, null);
		g.setColor(color1);
		g.setFont(font1);
		g.drawString(timer.toString(), 76 + xpos, 46 + ypos);
		g.drawString(addDecimals(clayMade),109+ xpos,68+ ypos);
		g.drawString(addDecimals(timer.getPerHour(clayMade)),117+ xpos,90+ ypos);
		g.drawString("" + rCount,75+xpos,112+ypos);
		
	}
	public class Area 
	{
	private Polygon p;

	/**
	 * Initializes a PolygonArea with the tiles given
	 *
	 * @param tiles
	 *            tiles to use in the area
	 */
		public Area(Tile... tiles) 
		{
			this.p = new Polygon();
			for (int i = 0; i < tiles.length; i++) 
			{
				p.addPoint(tiles[i].getX(), tiles[i].getY());
			}
		}

	/**
	 * Checks if a tile is in the area
	 *
	 * @param tile
	 *            The tile to check
	 * @return <b>true</b> if area does contain the tile, otherwise <b>false</b>
	 */
	public boolean contains(Tile tile)
	{
	    return this.contains(tile.getX(), tile.getY());
	}

	public boolean contains(int x, int y) 
	{
	    int i;
	    int j;
	    boolean result = false;
	    for (i = 0, j = p.npoints - 1; i < p.npoints; j = i++) 
	    {
	        if ((p.ypoints[i] > y - 1) != (p.ypoints[j] > y - 1)
	                && (x <= (p.xpoints[j] - p.xpoints[i]) * (y - p.ypoints[i])
	                / (p.ypoints[j] - p.ypoints[i]) + p.xpoints[i])) {
	            result = !result;
	        }
	    }
	    return result;
	}
	}

}
