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
import org.rev317.min.api.methods.Bank;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.SceneObject;
import org.rev317.min.api.wrappers.Tile;



@ScriptManifest(author = "NoiselessJoe", 
category = Category.OTHER, 
description = "Makes Gold Bars into Gold Leafs at the Crafting guild", 
name = "nGoldLeafs", 
servers = { "PKHonor" }, 
version = 1.1)
public class nGoldLeafs extends Script implements Paintable
{
	private final ArrayList<Strategy> strategies = new ArrayList<>();
	
	//Variables
	private Timer timer = new Timer();
	private int goldBar = 2358;
	private int goldLeaf = 8785;
	private int money = 996;
	private final Npc[] goldDude = Npcs.getNearest(4248);
	private final SceneObject[] Banks = SceneObjects.getNearest(2213);
	final Npc goldLeafMan = goldDude[0];
	final SceneObject booth = Banks[0];
	public boolean quit = false;
	public int XPRate;
	public int goldMade = 0;
	private static Image img1;
	private final Color color1 = new Color(255, 255, 255); /*White*/
    private final Font font1 = new Font("Verdana", 1, 11);/*Bold, Sized 11 Verdana Font*/
	private int[] randoms = { 410, 1091, 3117, 3022, 3351, 409 };
    private int rCount;
    private final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
    Area bobsIsland = new Area(new Tile(2511, 4765),
    new Tile(2511, 4790),
    new Tile(2542, 4790),
    new Tile(2542, 4765));
	
	
	public boolean onExecute()
	{
		img1 = getImage("http://i.imgur.com/GvxMtb0.png");
		strategies.add(new Antis());
		strategies.add(new makeLeafs());
		strategies.add(new Banking());
		
		provide(strategies);
		return true;
	}

public class makeLeafs implements Strategy
{

	@Override
	public boolean activate() 
	{
	
		return booth != null 
		&& goldLeafMan != null
		&& Inventory.getCount(goldBar) > 0
		&& !Players.getMyPlayer().isInCombat()
		&& Inventory.getCount(true, money) >= 150000;
	}

	@Override
	public void execute() 
	{	
		
		goldLeafMan.interact(2);
	
	Time.sleep(new SleepCondition() 
	{

		@Override
		public boolean isValid() {
			
			return Inventory.getCount(goldBar) == 0;
		}
	},3000);
	Time.sleep(500);
	}
	
	
}
public class Banking implements Strategy
{

	@Override
	public boolean activate() 
	{
		
		return booth != null
		&& goldLeafMan != null
		&& Inventory.getCount(goldBar) == 0
		&& !Players.getMyPlayer().isInCombat()
		&& quit == false;
	
	}

	@Override
	public void execute() 
	{
		
		
		if(booth != null)
		{
			/*If there is a bank booth near you, it will interact with it.*/
			booth.interact(0);
	
			Time.sleep(new SleepCondition() 
			{
				
				@Override
				public boolean isValid() 
				{
					/*Check if bank interface is open*/
					return Loader.getClient().getOpenInterfaceId() == 23350;
				}
			}, 10000);
			goldMade = goldMade + Inventory.getCount(goldLeaf);
					
			/*Deposists Everything into bank*/
			if(Loader.getClient().getOpenInterfaceId() == 23350)
			{
			Time.sleep(500);
				if(Inventory.getCount(goldLeaf) > 0)
				{
					Menu.sendAction(432, goldLeaf - 1, Inventory.getItems(goldLeaf)[0].getSlot(), 5064);
				}
			}
			Time.sleep(new SleepCondition() 
			{
				
				@Override
				public boolean isValid() 
				{
					
					/*When inventory is empty, it will withdrawl gold ore.*/
					return Inventory.getCount(goldLeaf) == 0;
				}
			}, 3000);
			Time.sleep(410);
			Menu.sendAction(53, goldBar - 1, 0, 5382);
			Time.sleep(new SleepCondition() 
			{
				
				@Override
				public boolean isValid() 
				{
					/*Checks if you got gold in your inventory*/
					return Inventory.getCount(goldBar) > 0;
				}
			}, 3000);
			if(Inventory.getCount(goldBar) == 0)
			{
				quit = true;
			}
			Time.sleep(650,1100);
		}
	
		}
		
	}
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

public void paint(Graphics arg0) 
{
	
	int xpos = 545;
	int ypos = 313;
	Graphics2D g = (Graphics2D) arg0;
	g.drawImage(img1,xpos, ypos, null);
	g.setColor(color1);
	g.setFont(font1);
	g.drawString(timer.toString(), xpos + 75, ypos + 53);
	g.drawString(addCommas(goldMade), xpos + 118, ypos + 75);
	g.drawString(addCommas(timer.getPerHour(goldMade)), xpos + 85, ypos + 97);
	g.drawString(addCommas(Inventory.getCount(true,money)), xpos + 85, ypos + 119);
	g.drawString("" + rCount, xpos + 74, ypos + 141);


}

/*Formats numbers, so 1000000 will output as 1,000,000*/
public String addCommas(int item)
{
    DecimalFormat form = new DecimalFormat("#,###");
    return "" + form.format(item);
}
/*Method to get Image from online via URL(ie. Imgur)*/
public static Image getImage(String url)
{
	try {
		
		return ImageIO.read(new URL(url));
	} catch (IOException e) 
	{
		return null;
	}
}
//Anti-Random Area
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


