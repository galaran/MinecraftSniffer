package me.galaran.mcsniffer;

import me.galaran.mcsniffer.packets.Packet;

public interface PacketHandler {

    public void handlePacket(Packet packet);
}
