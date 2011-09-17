package me.galaran.mcsniffer;

import me.galaran.mcsniffer.packets.Packet;

public interface PacketProcessor {

    public void processPacket(Packet packet);
}
