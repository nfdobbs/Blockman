package net.dobbs.dobbs_first_mod.tiles;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class TileManager {

    HashMap<String, ArrayList<Integer>> tileMap = new HashMap<String, ArrayList<Integer>>();


    //Checks if the tile is in the hashMap
    //If tile is in the hashmap it implies the player owns it

    public TileManager()
    {

    }
    public boolean isOwned(double x, double y, double z)
    {
        int tileNum;
        if(tileMap == null)
        {
            return false;
        }

        if(tileMap.containsKey(makeStringKey(x,z)) == true)
        {
            tileNum = getTileNumber(x,y,z);
            if(tileMap.get(makeStringKey(x,z)).contains(tileNum) == true)
            {
                return true;
            }
            else {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public static @NotNull String makeStringKey(double x, double z)
    {
        String key;
        key = "(" + (int)java.lang.Math.floor(x/16) + "," + (int)java.lang.Math.floor(z/16) + ")";
        return key;
    }

    public static int getTileNumber(double x, double y, double z)
    {
        //Calculating the true mod instead of remainder for wrap around
        x = (x%16 + 16)%16;
        y = y + 64;
        z = (z%16 + 16)%16;

        int tileNumber = (int)(java.lang.Math.floor(y/4)*16 + java.lang.Math.floor(x/4)*4 + java.lang.Math.floor(z/4));
        return tileNumber;
    }

    public void addTile(double x, double y, double z)
    {
        if(tileMap == null)
        {
            return;
        }

        else if(tileMap.containsKey("(0,0)") == false)
        {
            ArrayList<Integer> testing = new ArrayList<Integer>();
            testing.add(2);
            testing.add(18);
            tileMap.put("(0,0)", testing);
        }
        else
        {
            //System.out.println("Already Added")
        }

    }

}
