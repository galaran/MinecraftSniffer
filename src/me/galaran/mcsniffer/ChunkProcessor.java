package me.galaran.mcsniffer;


import me.galaran.mcsniffer.util.Chunk;

/**
 * Guarantees process non duplicate chunks
 * @author Galaran
 */
public interface ChunkProcessor {
    
    public void processChunk(Chunk chunk);
}
