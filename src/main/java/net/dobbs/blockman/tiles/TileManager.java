package net.dobbs.blockman.tiles;

import net.dobbs.blockman.events.HUDRenderer;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TileManager {

    HashMap<String, ArrayList<Integer>> tileMap = new HashMap<String, ArrayList<Integer>>();

    private int availableTiles = 0;


    //Checks if the tile is in the hashMap
    //If tile is in the hashmap it implies the player owns it
    public TileManager() {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(0);
        tileMap.put("availableTiles", temp);
    }

    public ArrayList<Integer> getTilesInChunk(int chunkX, int chunkZ)
    {
        if(!containsOwned(chunkX, chunkZ))
        {
            return null;
        }

        return tileMap.get("(" + chunkX + "," + chunkZ + ")");
    }

    public static Vec3d firstBlock(int tileNum, String chunks)
    {
        //Get Local Coordinates
        double x;
        double y;
        double z;

        y = java.lang.Math.floor(tileNum / 16);
        x = java.lang.Math.floor((tileNum - y*16)/4);
        z = (tileNum - y*16 - x*4)*4;

        y = y * 4;
        x = x * 4;

        //Transform to Global Coordinates
        y = y - 64;

        String[] splitChunks = chunks.split(",",2);

        splitChunks[0] = splitChunks[0].substring(1);
        splitChunks[1] = splitChunks[1].substring(0, splitChunks[1].length()-1);

        int xChunkMultiplier = Integer.parseInt(splitChunks[0]) * 16;
        int zChunkMultiplier = Integer.parseInt(splitChunks[1]) * 16;

        x = x + xChunkMultiplier;
        z = z + zChunkMultiplier;

        return new Vec3d(x,y,z);
    }

    public boolean containsOwned(int chunkX, int chunkZ)
    {
        String key = "(" + chunkX + "," + chunkZ + ")";

        if(tileMap == null)
        {
            return false;
        }

        return tileMap.containsKey(key);
    }

    public boolean isTileOwned(double x, double y, double z) {
        int tileNum;
        if (tileMap == null) {
            return false;
        }

        if (tileMap.containsKey(makeStringKey(x, z))) {
            tileNum = getTileNumber(x, y, z);
            if (tileMap.get(makeStringKey(x, z)).contains(tileNum)) {
                return true;
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static @NotNull String makeStringKey(double x, double z) {
        String key;
        key = "(" + (int) java.lang.Math.floor(x / 16) + "," + (int) java.lang.Math.floor(z / 16) + ")";
        return key;
    }

    public static int getTileNumber(double x, double y, double z) {
        //Calculating the true mod instead of remainder for wrap around
        x = (x % 16 + 16) % 16;
        y = y + 64;
        z = (z % 16 + 16) % 16;

        return (int) (Math.floor(y / 4) * 16 + Math.floor(x / 4) * 4 + Math.floor(z / 4));
    }

    public void addTile(double x, double y, double z) {
        if (tileMap == null) {
            return;
        } else if (tileMap.containsKey(makeStringKey(x, z)) == true) {
            tileMap.get(makeStringKey(x, z)).add(getTileNumber(x, y, z));

        } else {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            temp.add(getTileNumber(x, y, z));
            tileMap.put(makeStringKey(x, z), temp);

            //System.out.println("Added Tile at " + makeStringKey(x, z) + " Tile Number: " + getTileNumber(x, y, z));
        }
    }

    public void addTile(int chunkX, int chunkZ, int tileNum)
    {
        String key = "(" + chunkX + "," + chunkZ + ")";
        if(tileMap == null){
        }

        else if(tileMap.containsKey(key))
        {
            tileMap.get(key).add(tileNum);
        }
        else
        {
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(tileNum);
            tileMap.put(key, temp);
        }
    }

    public byte[] serializeTileMap() throws IOException {

        tileMap.get("availableTiles").set(0, availableTiles);

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteArray);
        out.writeObject(tileMap);

        return byteArray.toByteArray();
    }

    public void deSerializeTileMap(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(byteArray);

        tileMap = (HashMap<String, ArrayList<Integer>>) in.readObject();

        availableTiles = tileMap.get("availableTiles").get(0);
    }

    public void increaseAvailableTiles(int num)
    {
        availableTiles += num;
    }

    public void decreaseAvailableTiles(int num)
    {
        availableTiles -= num;
    }

    public void setAvailableTiles(int num)
    {
        availableTiles = num;
    }

    public int getAvailableTiles()
    {
        return availableTiles;
    }
}
