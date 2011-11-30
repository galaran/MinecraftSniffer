package me.galaran.mcsniffer.util;

import java.util.Iterator;

/**
 * Absolute coordinates
 */
public class Chunk implements Iterable<Vec3D> {
    
    private final byte[] data; // decompressed
    public final int xOffset;
    public final int zOffset;
    
    public Chunk(byte[] decompressedChunkData, int xOff, int zOff) {
        data = decompressedChunkData;
        xOffset = xOff;
        zOffset = zOff;
    }
    
    /**
     * @param coord absolute vector
     * @return block id or -1 if not in this chunk
     */
    public byte getId(Vec3D coord) {
        int xInChunk = coord.x - xOffset;
        int zInChunk = coord.z - zOffset;
        int idx = (xInChunk << 11) + (zInChunk << 7) + coord.y;
        if (idx < 0 || idx >= 32_768) { // 128 * 16 * 16
            return -1;
        }
        
        return data[idx];
    }
    
    public Block getBlockAt(Vec3D c) {
        byte blockId = getId(c);
        if (blockId == -1) {
            return null;
        }
        return new Block(c, blockId);
    }

    @Override
    public Iterator<Vec3D> iterator() {
        return new Iterator<Vec3D>() {
            
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return (idx < 32_768);
            }

            @Override
            public Vec3D next() {
                int x = xOffset + (idx >> 11);
                int y = idx & 127;
                int z = zOffset + ((idx >> 7) & 15);

                idx++;

                return new Vec3D(x, y, z);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported");
            }
        };
    }
}
