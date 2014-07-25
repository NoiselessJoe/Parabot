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
import org.parabot.environment.api.utils.Timer;


@ScriptManifest
(author = "NoiselessJoe", 
category = Category.MINING, 
description = "Mines marble blocka at the crafting guild and banks them.",
name = "nMarble", 
servers = { "PKHonor" },
version = 1)
public class nMarble extends Script implements Paintable{
	
	private final ArrayList<Strategy> strategies = new ArrayList<>();
	
	private Timer timer = new Timer();
	private int marbleBlock = 8787;
	private int chisel = 1756;
	private int hammer = 2348;
	private int marbleRock = 20359;
	private int bank = 2213;		
	private int miningAnim = 625;
	private int AFK = 1353;
	private static int startingXP = Skill.CRAFTING.getExperience();
	public int currentXP;
	public int marbleCount = 0;
	public int byThisMuch;
	private static Image img1;
	private final Color color1 = new Color(0, 0, 0); /*Black*/
    private final Font font1 = new Font("Verdana", 1, 9);/*Bold, Sized 9 Verdana Font*/
	private final SceneObject[] Banks = SceneObjects.getNearest(bank);
	private final SceneObject[] Marble = SceneObjects.getNearest(marbleRock);
	public boolean stoppls = false;
	final SceneObject booth = Banks[0];
	final SceneObject whiteRock = Marble[0];
	private int[] randoms = { 410, 1091, 3117, 3022, 3351, 409 };
    private int rCount;
    private final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
    Area bobsIsland = new Area(new Tile(2511, 4765),
    new Tile(2511, 4790),
    new Tile(2542, 4790),
    new Tile(2542, 4765));

public boolean onExecute()
{
	strategies.add(new Antis());
	strategies.add(new Banking());
	strategies.add(new Mine());
	strategies.add(new whileMining());
	provide(strategies);
	return true;

}

public class Mine implements Strategy
{

	@Override
	public boolean activate() 
	{
		img1 = getImage("http://i.imgur.com/uuQXoC1.png");
		
		return whiteRock != null
		&& !Inventory.isFull()
		&& Players.getMyPlayer().getAnimation() == -1
		&& Inventory.getCount(chisel) > 0
		&& Inventory.getCount(hammer) > 0
		&& Inventory.getCount(marbleBlock) >= 0
		&& !bobsIsland.contains(Players.getMyPlayer().getLocation());
	}

	@Override
	public void execute()
	{
		try
		{
		if(whiteRock != null && Players.getMyPlayer().getAnimation() != miningAnim)
		{
		
			Time.sleep(300);
		whiteRock.interact(0);
		Time.sleep(new SleepCondition() 
		{

			@Override
			public boolean isValid()
			{
				return Players.getMyPlayer().getAnimation() == miningAnim;
			}
			
		}
		,6000);
		}
		}
		catch (NullPointerException | ArrayIndexOutOfBoundsException e)
        {
            System.out.println(e.getMessage());
            sleep(1000);
        }
	
	
	
	}
}
public class whileMining implements Strategy
{

	@Override
	public boolean activate() 
	{
		return whiteRock != null			
		&& Inventory.getCount(marbleBlock) == 19
		&& Players.getMyPlayer().getAnimation() == miningAnim
				&& !bobsIsland.contains(Players.getMyPlayer().getLocation());
	}

	@Override
	public void execute() 
	{
		if(whiteRock != null)
		{
			Time.sleep(new SleepCondition() 
		
		{
			
			@Override
			public boolean isValid() 
			{
				
				return Inventory.getCount(marbleBlock) == 20;
			}
		}, 7000);
		whiteRock.interact(0);
		}
	}

}
public class Banking implements Strategy
{

	@Override
	public boolean activate() {
		
		return booth != null 
		&& Inventory.getCount(marbleBlock) == 26
		&& Players.getMyPlayer().getAnimation() == -1
		&& whiteRock != null
		&& !bobsIsland.contains(Players.getMyPlayer().getLocation());
	}

	@Override
	public void execute() 
	{
		
		try
		{
			
			if(booth != null)
			{
				Menu.sendAction(502, booth.getHash(), booth.getLocalRegionX(), booth.getLocalRegionY());
				byThisMuch = Inventory.getCount(marbleBlock);
			marbleCount = marbleCount + byThisMuch;	
			/*If there is a bank booth near you, it will interact with it.*/
				
				Time.sleep(new SleepCondition() 
				{
					
					@Override
					public boolean isValid() 
					{
						/*Check if bank interface is open*/
						return Loader.getClient().getOpenInterfaceId() == 23350;
					}
				}, 6000);
				if(Loader.getClient().getOpenInterfaceId() == 23350)
				{/*Deposists All Marble Blocks into bank*/
				Menu.sendAction(432, marbleBlock - 1, Inventory.getItems(marbleBlock)[0].getSlot(), 5064);
				
				Time.sleep(new SleepCondition() 
				{
					
					@Override
					public boolean isValid() 
					{
						return Inventory.getCount(marbleBlock) == 0;
					}
				}, 3000);
				Time.sleep(300);
				}
				}
		}
		catch (NullPointerException | ArrayIndexOutOfBoundsException e)
        {
            System.out.println(e.getMessage());
            sleep(1000);
        }
	}

}
//Thanks to Minimal for the Anti-Randoms
//http://www.parabot.org/community/user/10775-minimal/
	public class Antis implements Strategy
  {
		@Override
		public boolean activate()
      {
          for (Npc n : Npcs.getNearest(randoms))
          {
              if (n.getLocation().distanceTo() < 3)
                  return true;
          }

          return false;
      }
      @Override
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
	currentXP = Skill.CRAFTING.getExperience() - startingXP;
	int xpos = 545;
	int ypos = 313;
	Graphics2D g = (Graphics2D) arg0;
	g.drawImage(img1,xpos, ypos, null);
	g.setColor(color1);
	g.setFont(font1);
	g.drawString(timer.toString(), 63 + xpos, 50 + ypos);
	g.drawString(addDecimals(marbleCount),87+ xpos,69+ ypos);
	g.drawString(addDecimals(timer.getPerHour(marbleCount)),87+ xpos,86+ ypos);
	g.drawString(addDecimals(currentXP),113+ xpos, 103 + ypos);
	g.drawString(addDecimals(timer.getPerHour(currentXP)),67+ xpos,122+ ypos);
	g.drawString("" + rCount,100+xpos,141+ypos);
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
