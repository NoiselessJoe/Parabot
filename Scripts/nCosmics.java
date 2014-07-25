package PKHonorRuneCrafter;


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
import org.rev317.min.api.methods.Bank;
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
(author = "NoiselessJoe", category = Category.RUNECRAFTING, description = "Makes cosmic runes. Put essence in first bank slot, and start in cosmic alter rift. Now with Anti-Randoms (Thanks Minimal) & Paint.", name = "nCosmics", servers = {"PkHonor"}, version = 1.4)
/**********************************************************************************************************************/
public class nCosmics extends Script implements Paintable
{
	private final ArrayList<Strategy> strategies = new ArrayList<>();
	
	/*Variables*/
	
	/*Makes a new timer, used for getPerHour*/
	private Timer timer = new Timer();
	/*Sceneobjects to fins the nearest bank/alter*/
	private final SceneObject[] Banks = SceneObjects.getNearest(2213);
	private final SceneObject[] Altar = SceneObjects.getNearest(2484);
	final SceneObject booth = Banks[0];
	/*Essence and Cosmic Rune IDs*/
	private static int ESSENCE = 1437;
    private int COSMIC = 565;
    /*Anti-Random vars*/
    private int[] randoms = { 410, 1091, 3117, 3022, 3351, 409 };
    private int rCount;
    private final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
    private static Image img1;
    Area bobsIsland = new Area(new Tile(2511, 4765),
    new Tile(2511, 4790),
    new Tile(2542, 4790),
    new Tile(2542, 4765));
    /*Paint Vars*/
    private final Color color1 = new Color(255, 255, 255); /*White*/
    private final Font font1 = new Font("Verdana", 1, 11);/*Bold, Sized 11 Verdana Font*/
    public int cosmicsMade = 0;
    public int byhow;
	
	public boolean onExecute()
	
	{
		if(Skill.RUNECRAFTING.getLevel() >= 59)
		{
			byhow = 56;
		}
		else
		{
			byhow = 28;
		}
		strategies.add(new Antis());
		strategies.add(new makeRunes());
		strategies.add(new Banking());
		
		provide(strategies);
		return true;
		
	}
	public void onFinish()
	{
		System.out.println("You have made " + cosmicsMade + " Cosmic Runes.");
		System.out.println("Thanks for using my script! :) Any problems, let me know in the thread. ");
	}
	/**********************************************************************************************************************/
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
	/**********************************************************************************************************************/
	//Interacts with Alter
	public class makeRunes implements Strategy
{
    final SceneObject CosmicAlter = Altar[0];
	
	@Override
	public boolean activate() {

		return Inventory.isFull()
		&& !Players.getMyPlayer().isInCombat()
		&& Inventory.getCount(ESSENCE) > 0;
	}

	@Override
	public void execute() 
	{
		
		if(CosmicAlter != null)
		{
			CosmicAlter.interact(0);
			Time.sleep(420);
			cosmicsMade = cosmicsMade + byhow;
		}
		
	}
	
}
	/**********************************************************************************************************************/
	
	/*Banks the Cosmic Runes, withdrawls Rune Essence*/
	
	public class Banking implements Strategy
{
	
	
@Override
	public boolean activate() 
	{
		return !Inventory.isFull()
		&& !Players.getMyPlayer().isInCombat();
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
			}, 3000);
			
			/*Deposists Everything into bank*/
			Menu.clickButton(23412);
			Time.sleep(new SleepCondition() 
			{
				
				@Override
				public boolean isValid() 
				{
					
					//When inventory is empty, it will withdrawl the essence.
					return Inventory.isEmpty();
				}
			}, 3000);
			Menu.sendAction(53, ESSENCE - 1, 0, 5382);
			Time.sleep(new SleepCondition() 
			{
				
				@Override
				public boolean isValid() 
				{
					return Inventory.getCount(ESSENCE) > 0;
				}
			}, 3000);
			Time.sleep(300);
		}
	}
}
	/*********************************************************************************/
	/*Formats numbers, so 1000000 will output as 1,000,000*/
	public String addDecimals(int thing)
    {
        DecimalFormat form = new DecimalFormat("#,###");
        return "" + form.format(thing);
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
	/*Makes the Paint*/
	public void paint(Graphics arg0)
	
	{
/*	Using xpos and ypos makes it easier to find the coords on the paint. I get the
	coords of the paint themselves and add the xpos and ypos to get the location.
*/
		int xpos = 545;
		int ypos = 313;
		img1=getImage("http://i.imgur.com/fBoKdBE.png");
		Graphics2D g = (Graphics2D) arg0;
		g.drawImage(img1, xpos,ypos,null);
		g.setColor(color1);
		g.setFont(font1);
		g.drawString(timer.toString(), 70+xpos, 82+ypos);
		g.drawString("" + addDecimals(cosmicsMade), 95+xpos,100+ypos );
		g.drawString("" + addDecimals(timer.getPerHour((int)cosmicsMade)), 102+xpos,118+ypos );
		g.drawString("" + rCount, 109 + xpos, 136 + ypos);
	}
	//Area for the Anti-Randoms (from Minimal)
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
