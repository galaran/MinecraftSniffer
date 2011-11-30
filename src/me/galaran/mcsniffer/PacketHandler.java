package me.galaran.mcsniffer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import me.galaran.mcsniffer.packets.Packet;
import me.galaran.mcsniffer.packets.Packet33MapChunk;
import me.galaran.mcsniffer.util.Vec3D;

class PacketHandler {
    
    private static final Logger log = Logger.getLogger("galaran.diamf.sniffer");
    private Map<Byte, PacketProcessor> packetProcessors = new HashMap<>();
    private ChunkProcessor chunkProcessor = null;
    private Set<Vec3D> alreadyProcessedChunks = null;
    
    public synchronized void registerHandler(byte packetCode, PacketProcessor handler) {
        packetProcessors.put(packetCode, handler);
    } 
    
    /**
     * Only for map chunk packet. Guarantees process chunks without duplicates
     */
    public void registerChunkHandler(ChunkProcessor chProc) {
        chunkProcessor = chProc;
        alreadyProcessedChunks = new HashSet<>();
    }
    
    /**
     * First process chunks, later packet - for map chunk packet
     */
    public void processPacket(Packet packet) {
        // chunk packet - process it
        if (chunkProcessor != null && packet.code == (byte)0x33) {
            Packet33MapChunk chunkPacket = (Packet33MapChunk) packet;
            
            Vec3D chunkVec = new Vec3D(chunkPacket.chunk.xOffset, 0, chunkPacket.chunk.zOffset);
            if (!alreadyProcessedChunks.contains(chunkVec)) {
                // process this new chunk and mark it
                chunkProcessor.processChunk(chunkPacket.chunk);
                alreadyProcessedChunks.add(chunkVec);
            } else {
                log.info("Duplicated chunk: " + chunkVec);
            }
        }
        
        // general packet
        PacketProcessor procForThis = packetProcessors.get(packet.code);
        if (procForThis != null)
            procForThis.processPacket(packet);
    }
}
