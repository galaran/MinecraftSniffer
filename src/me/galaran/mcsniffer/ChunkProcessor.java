package me.galaran.mcsniffer;

import java.util.List;
import me.galaran.mcsniffer.util.Block;

/**
 * Guarantees process non duplicate chunks
 * @author Galaran
 */
public interface ChunkProcessor {
    
    public void processChunk(List<Block> chunk);
}
