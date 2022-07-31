package net.dobbs.blockman.events;

import net.dobbs.blockman.util.PlayerAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static net.dobbs.blockman.events.ServerEvents.blockManIdentifier;

public class ClientEvents {
    public static final String MOD_ID = "blockman";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init()
    {
        //Client Packet Received Event
        ClientPlayNetworking.registerGlobalReceiver(blockManIdentifier,(client, handler, buf, responseSender) -> {
            byte[] receivedBytes = buf.readByteArray();
            LOGGER.info("Received Packet: " + Arrays.toString(receivedBytes));

            client.execute(() -> {
                try {
                    assert client.player != null;
                    ((PlayerAccess)client.player).deSerializeTileMap(receivedBytes);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                ((PlayerAccess)client.player).addTile(0, -1, 16);
                ((PlayerAccess)client.player).addTile(-1, 0, 16);
            });
        });


    }
}
