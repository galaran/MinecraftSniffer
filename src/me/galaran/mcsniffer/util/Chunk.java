package me.galaran.mcsniffer.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Absolute coordinates
 */
public class Chunk {
    
    private final Map<Coord, Byte> chunkContent = new HashMap<Coord, Byte>();
    public final int xOffset;
    public final int zOffset;
    
    public Chunk(byte[] decompressedChunkData, int xOff, int zOff) {
        xOffset = xOff;
        zOffset = zOff;
        
        int x, y, z;
        for (int i = 0; i < 128 * 16 * 16; i++) {
            x = xOff + (i >> 11);
            y = i & 127;
            z = zOff + ((i >> 7) & 15);
            chunkContent.put(new Coord(x, y, z), decompressedChunkData[i]);
        }
    }
    
    public byte getId(Coord coord) {
        return chunkContent.get(coord);
    }
    
    public Set<Coord> getCoordSet() {
        return chunkContent.keySet();
    }
    
    public Block getBlockAt(Coord c) {
        return new Block(c, chunkContent.get(c));
    }
}
