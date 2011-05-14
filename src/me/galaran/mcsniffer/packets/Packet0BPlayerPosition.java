package me.galaran.mcsniffer.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import me.galaran.mcsniffer.util.Coord;
import me.galaran.mcsniffer.util.DoubleCoord;

public class Packet0BPlayerPosition extends Packet {
    
    private static final Logger log = Logger.getLogger("galaran.diamf.diamond_finder");
    
    private double         x; //1
    private double         y; //9
    private double         stance; //17
    private double         z; //25
    private boolean        onGround; //33
    
    public DoubleCoord playerCoords;

    @Override
    public void readPacket(ByteBuffer packet) throws Exception {
        x = packet.getDouble();
        y = packet.getDouble();
        stance = packet.getDouble();
        z = packet.getDouble();
        onGround = (packet.get() == 1) ? true : false;
        playerCoords = new DoubleCoord(x, y, z);
    }

    @Override
    public boolean validate(ByteBuffer buff)  throws BufferUnderflowException {

        if ( Math.abs(buff.getDouble()) > Coord.MAX_X_ABS ) return false; // player cannot walk so far ;)
        double y = buff.getDouble();
        if ( y < Coord.PLAYER_MIN_Y || y > Coord.PLAYER_MAX_Y ) return false; // player cannot fall down or fly so far ;)
        //stance
        double stance = buff.getDouble();
        if (stance - y < 0.1 || stance - y > 1.65) return false; // Illegal Stance
        if ( Math.abs(buff.getDouble()) > Coord.MAX_Z_ABS ) return false; // player cannot walk so far ;)
        
        byte onGround = buff.get();
        if ( !(onGround == 0 || onGround == 1) ) return false; // invalid onGround
        
        return true;
    }

}
