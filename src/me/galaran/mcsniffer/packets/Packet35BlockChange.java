package me.galaran.mcsniffer.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import me.galaran.mcsniffer.util.Block;
import me.galaran.mcsniffer.util.Vec3D;

public class Packet35BlockChange extends Packet {
    
    private static final Logger log = Logger.getLogger("galaran.diamf.sniffer");
    
    private int         X; //1
    private byte        Y; //5
    private int         Z; //6
    private byte        newType; //10
    private byte        meta; //11
    
    public Block blockChange;

    @Override
    public void readPacket(ByteBuffer packet) throws Exception {
        X = packet.getInt();
        Y = packet.get();
        Z = packet.getInt();
        newType = packet.get();
        meta = packet.get();
        
        blockChange = new Block(X, Y, Z, newType);
    }

    @Override
    public boolean validate(ByteBuffer buff) throws BufferUnderflowException {
        
        if (Math.abs(buff.getInt()) > Vec3D.MAX_X_ABS) return false; // player cannot walk so far ;)
        byte y = buff.get();
        if (y < 0) return false; // y always positive
        if (Math.abs(buff.getInt()) > Vec3D.MAX_Z_ABS) return false; // player cannot walk so far ;)
        
        if (buff.get() < 0) return false; // new type negative
        
        // len check
        buff.get();
        
        return true;
    }

}
