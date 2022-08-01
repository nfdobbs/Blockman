package net.dobbs.blockman.events;

import net.dobbs.blockman.util.PlayerAccess;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerEvents {
    public static final String MOD_ID = "blockman";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier blockManIdentifier = new Identifier("blockman");

    public static void init(){
        //Server Connect Event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            String path = server.getSavePath(WorldSavePath.PLAYERDATA).toString();
            String uniqueID = handler.player.getUuidAsString();

            path += "\\" + uniqueID + ".blockman";

            LOGGER.info(path);
            byte [] serializedTileMap;
            File file = new File(path);

            //Has Player Data
            if(file.isFile())
            {
                LOGGER.info(handler.player.getEntityName() + " has data");
                //Load data to into tileManager

                try {
                    serializedTileMap = Files.readAllBytes(Path.of(path));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    ((PlayerAccess)handler.player).deSerializeTileMap(serializedTileMap);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
            //New Player Connecting
            else
            {
                LOGGER.info(handler.player.getEntityName() + " is connecting for the first time!");

                //Add first tile to tileManager
                ((PlayerAccess)handler.player).addTile(handler.player.getX(), handler.player.getY(), handler.player.getZ());
                LOGGER.info("Added first tile for " + handler.player.getEntityName());

                try {
                    serializedTileMap = ((PlayerAccess)handler.player).serializeTileMap();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            //Send HashMap to Client
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeByteArray(serializedTileMap);
            ServerPlayNetworking.send(handler.player, blockManIdentifier,buffer);

            LOGGER.info("Sent Packet: " + serializedTileMap.toString());
        });

        //Server Disconnect Event
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            LOGGER.info(handler.player.getEntityName() + " has disconnected");

            String path = server.getSavePath(WorldSavePath.PLAYERDATA).toString();
            String uniqueID = handler.player.getUuidAsString();

            path +=  "\\" + uniqueID + ".blockman";

            byte[] serializedTileMap;
            try {
                serializedTileMap = ((PlayerAccess)handler.player).serializeTileMap();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                Files.write(Path.of(path), serializedTileMap);
                LOGGER.info("Wrote data file to " + Path.of(path).toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //Server Death Event (Hot Fix)
        {
            ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, server) -> {

                byte[] oldTileMap;

                try {
                    oldTileMap = ((PlayerAccess)oldPlayer).serializeTileMap();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    ((PlayerAccess)newPlayer).deSerializeTileMap(oldTileMap);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeByteArray(oldTileMap);
                ServerPlayNetworking.send(newPlayer, blockManIdentifier,buffer);
            });
        }
    }
}
