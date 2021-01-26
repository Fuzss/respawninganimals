package com.fuzs.puzzleslib_ra.network.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;

/**
 * network message template
 */
public interface IMessage {

    /**
     * writes message data to buffer
     * @param buf network data byte buffer
     */
    void writePacketData(final PacketBuffer buf);

    /**
     * reads message data from buffer
     * @param buf network data byte buffer
     */
    void readPacketData(final PacketBuffer buf);

    /**
     * call {@link #readPacketData} and return this
     * @param buf network data byte buffer
     * @param <T> this
     * @return instance of this
     */
    @SuppressWarnings("unchecked")
    default <T extends IMessage> T getPacketData(PacketBuffer buf) {

        this.readPacketData(buf);
        return (T) this;
    }

    /**
     * handles message on receiving side
     * @param player server player when sent from client
     */
    void processPacket(@Nullable ServerPlayerEntity player);

}
