package me.galaran.mcsniffer.packets;

import java.nio.ByteBuffer;

public abstract class Packet implements PacketValidator {
  
    public byte code = -1;
    
    /**
     * @param buff Bytebuffer with pos on packet begin (after packet code). Change pos to next byte of packet
     */
    public abstract void readPacket(ByteBuffer packet) throws Exception;

}
