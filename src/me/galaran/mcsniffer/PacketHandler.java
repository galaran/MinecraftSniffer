package me.galaran.mcsniffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.galaran.mcsniffer.packets.Packet;
import me.galaran.mcsniffer.packets.Packet33MapChunk;
import me.galaran.mcsniffer.util.Coord;

class PacketHandler {
    
    private static final Logger log = Logger.getLogger("galaran.diamf.diamond_finder");
    private Map<Byte, PacketProcessor> packetProcessors = new HashMap<Byte, PacketProcessor>();
    private ChunkProcessor chunkProcessor = null;
    private List<Coord> alreadyProcessedChunks = null;
    
    public synchronized void registerHandler(byte packetCode, PacketProcessor handler) {
        packetProcessors.put(packetCode, handler);
    } 
    
    /**
     * Only for map chunk packet. Guarantees process chunks without duplicates
     */
    public void registerChunkHandler(ChunkProcessor ch) {
        chunkProcessor = ch;
        alreadyProcessedChunks = new ArrayList<Coord>();
    }
    
    /**
     * First process chunks, later packet - for map chunk packet
     */
    public void processPacket(Packet packet) {
        // chunk packet - process it
        if (chunkProcessor != null && packet.code == (byte)0x33) {
            Packet33MapChunk chunkPacket = (Packet33MapChunk)packet;
            
            Coord curChunkStartCoord = new Coord(chunkPacket.chunk.xOffset, chunkPacket.chunk.zOffset);
            if (!alreadyProcessedChunks.contains(curChunkStartCoord)) {
                // convert block list to map
                chunkProcessor.processChunk(chunkPacket.chunk);
            } else {
                log.info("Duplicated chunk starts at: " + curChunkStartCoord);
            }
            
            alreadyProcessedChunks.add(curChunkStartCoord);
        }
        
        // general packet
        PacketProcessor procForThis = packetProcessors.get(packet.code);
        if (procForThis != null)
            procForThis.processPacket(packet);
    }
}
