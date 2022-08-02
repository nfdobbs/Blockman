package net.dobbs.blockman.events;

import net.dobbs.blockman.Blockman;
import net.dobbs.blockman.tiles.TileManager;
import net.dobbs.blockman.util.PlayerAccess;
import net.dobbs.blockman.util.PlayerMoveCallback;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;

public class PlayerCollision {
    private static final DecimalFormat rounder = new DecimalFormat("0.00");

    public static void init()
    {
        PlayerMoveCallback.EVENT.register((player, collisionVector) -> {

           ///Rendering Stuff///
            World world = player.getWorld();
            HUDRenderer hud = HUDRenderer.getInstance();

            if(!world.isClient) {

                //System.out.println(((PlayerAccess)player).doesPlayerOwn(player.getX(), player.getY(), player.getZ()));
            }

            if(world.isClient){
                int tileNum;
                String chunk;
                hud.positionLine = "Position: " + rounder.format(player.getPos().x) + ", " + rounder.format(player.getPos().y) + ", " + rounder.format(player.getPos().z);
                chunk = TileManager.makeStringKey(player.getPos().x, player.getPos().z);
                hud.chunkLine = "Chunk: " + chunk;
                tileNum = TileManager.getTileNumber(player.getPos().x, player.getPos().y, player.getPos().z);
                hud.tileLine = "Tile: " + tileNum;

                hud.predictedLine = "Collision: " + collisionVector;

                if(((PlayerAccess)player).doesPlayerOwn(player.getX(), player.getY(), player.getZ()))
                {
                    hud.tileOwnedLine = "True";
                   hud.tileOwnedColor = 0x00ff00;
                }
                else
                {
                    hud.tileOwnedLine = "False";
                    hud.tileOwnedColor = 0xff0000;
                }
            }

            hud.availableTilesLine = " " + ((PlayerAccess)player).getAvailableTiles();

            ///Rendering Stuff///


            Vec3d playerPosition = player.getPos();
            Vec3d returnVector = collisionVector;
            Box playerBox = player.getBoundingBox();

            int tileNum = TileManager.getTileNumber(playerPosition.x, playerPosition.y, playerPosition.z);
            String chunk = TileManager.makeStringKey(playerPosition.x, playerPosition.z);

            Vec3d lowestTileBlock = TileManager.firstBlock(tileNum, chunk);

            double yMovement = collisionVector.y;
            double xMovement = collisionVector.x;
            double zMovement = collisionVector.z;

            ((PlayerAccess)player).doesPlayerOwn(playerPosition.x, (playerBox.minY + collisionVector.y) , playerPosition.z);

            if(((PlayerAccess)player).doesPlayerOwn(playerPosition.x, playerPosition.y, playerPosition.z))
            {
                //Checking Y
                if (collisionVector.y < 0) //-Y Falling
                {
                    if (!((PlayerAccess) player).doesPlayerOwn(playerPosition.x, (playerBox.minY + collisionVector.y), playerPosition.z))
                        yMovement = lowestTileBlock.y - playerPosition.y;
                }

                else if (collisionVector.y > 0) //+Y Jumping
                {
                    if (!((PlayerAccess) player).doesPlayerOwn(playerPosition.x, (playerBox.maxY + collisionVector.y), playerPosition.z))
                        yMovement = (lowestTileBlock.y+4) - playerBox.maxY;
                }

                if (collisionVector.x < 0) //-X Heading West
                {
                    if (!((PlayerAccess) player).doesPlayerOwn((playerBox.minX + collisionVector.x), playerPosition.y, playerPosition.z))
                        xMovement = (lowestTileBlock.x) - playerBox.minX;
                }

                else if (collisionVector.x > 0) // +X Heading East
                {
                    if (!((PlayerAccess) player).doesPlayerOwn((playerBox.maxX + collisionVector.x), playerPosition.y, playerPosition.z))
                        xMovement = (lowestTileBlock.x+4) - playerBox.maxX;
                }

                if(collisionVector.z < 0) //-Z Heading North
                {
                    if (!((PlayerAccess) player).doesPlayerOwn(playerPosition.x, playerPosition.y, (playerBox.minZ + collisionVector.z)))
                        zMovement = lowestTileBlock.z - playerBox.minZ;
                }

                else if(collisionVector.z > 0)//+Z Heading South
                {
                    if (!((PlayerAccess) player).doesPlayerOwn(playerPosition.x, playerPosition.y, (playerBox.maxZ + collisionVector.z)))
                        zMovement = (lowestTileBlock.z + 4) - playerBox.maxZ;
                }

                if (Math.abs(yMovement) < 1.0E-7)
                    yMovement = 0.0;

                if(Math.abs(xMovement) < 1.0E-7)
                    xMovement = 0.0;


                if(Math.abs(zMovement) < 1.0E-7)
                    zMovement = 0.0;


                returnVector = new Vec3d(xMovement, yMovement, zMovement);
            }

            return returnVector;
        });
    }
}
