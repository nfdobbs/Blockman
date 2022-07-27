package net.dobbs.dobbs_first_mod.events;

import net.dobbs.dobbs_first_mod.util.PlayerAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static net.dobbs.dobbs_first_mod.events.ServerEvents.blockManIdentifier;

public class ClientEvents {
    public static final String MOD_ID = "dobbs_first_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init()
    {
        //Client Packet Received Event
        ClientPlayNetworking.registerGlobalReceiver(blockManIdentifier,(client, handler, buf, responseSender) -> {
            byte[] receivedBytes = buf.readByteArray();
            LOGGER.info("Received Packet: " + receivedBytes.toString());

            client.execute(() -> {
                try {
                    ((PlayerAccess)client.player).deSerializeTileMap(receivedBytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
