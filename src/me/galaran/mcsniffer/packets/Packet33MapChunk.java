package me.galaran.mcsniffer.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.Inflater;

import me.galaran.mcsniffer.util.Block;
import me.galaran.mcsniffer.util.Coord;

public class Packet33MapChunk extends Packet {
    
    private static final Logger log = Logger.getLogger("galaran.diamf.diamond_finder");
    
    public int         X; //1
    public short       Y; //5
    public int         Z; //7
    public byte        sizeX; //11
    public byte        sizeY; //12
    public byte        sizeZ; //13
    public int         compressedLength; //14
    private byte[]     compressedData; //18
    
    public byte[]      decompressedData;
    public List<Block> blocks = new ArrayList<Block>();
    
    private static final Inflater inflater = new Inflater();

    
    @Override
    public void readPacket(ByteBuffer packet) throws Exception {
        X = packet.getInt();
        Y = packet.getShort();
        Z = packet.getInt();
        sizeX = (byte) (packet.get() + 1);
        sizeY = (byte) (packet.get() + 1);
        sizeZ = (byte) (packet.get() + 1);
        compressedLength = packet.getInt();
        compressedData = new byte[compressedLength];
        packet.get(compressedData);
        
        synchronized (inflater) {
            inflater.reset();
            inflater.setInput(compressedData);
            decompressedData = new byte[81920];
            inflater.inflate(decompressedData);
        }
        
        int x, y, z;
        for (int i = 0; i < 128 * 16 * 16; i++) {
            x = X + (i >> 11);
            y = i & 127;
            z = Z + ((i >> 7) & 15);
            blocks.add(new Block(x, y, z, decompressedData[i]));
        }
    }

    @Override
    public boolean validate(ByteBuffer buff) throws BufferUnderflowException {
        
        if (Math.abs(buff.getInt()) > Coord.MAX_X_ABS) return false; // player cannot walk so far ;)
        if (buff.getShort() != 0) return false; // chunk y always 0
        if (Math.abs(buff.getInt()) > Coord.MAX_Z_ABS) return false; // player cannot walk so far ;)
        
        // chunk bounds
        if (buff.get() != 15) return false;
        if (buff.get() != 127) return false;
        if (buff.get() != 15) return false;
        
        // len check
        int expectedLen = buff.getInt();
        byte[] data = new byte[expectedLen];
        buff.get(data);
        
        return true;
    }
}
