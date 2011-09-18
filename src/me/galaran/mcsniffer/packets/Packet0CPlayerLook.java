package me.galaran.mcsniffer.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import me.galaran.mcsniffer.util.Coord;

public class Packet0CPlayerLook extends Packet {
    
    private static final Logger log = Logger.getLogger("galaran.diamf.sniffer");
    
    public float         yaw; //1
    public float         pitch; //5
    public boolean       onGround; //9

    @Override
    public void readPacket(ByteBuffer packet) throws Exception {
        yaw = packet.getFloat();
        pitch = packet.getFloat();
        onGround = (packet.get() == 1) ? true : false;
    }

    @Override
    public boolean validate(ByteBuffer buff) throws BufferUnderflowException {

        float yaw = buff.getFloat();
        if (Math.abs(yaw) > Coord.MAX_YAW_ABS) return false; // invalid vertical yaw
        // super hack
        if (String.valueOf(yaw).contains("E")) return false;
        
        if (Math.abs(buff.getFloat()) > Coord.MAX_PITCH_ABS) return false; // invalid vertical pitch
        
        byte onGround = buff.get();
        if ( !(onGround == 0 || onGround == 1) ) return false; // invalid onGround
        
        return true;
    }


}
