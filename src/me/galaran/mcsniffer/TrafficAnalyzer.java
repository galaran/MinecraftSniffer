package me.galaran.mcsniffer;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.galaran.mcsniffer.packets.Packet;
import me.galaran.mcsniffer.packets.PacketValidator;
import me.galaran.mcsniffer.packets.Packet0BPlayerPosition;
import me.galaran.mcsniffer.packets.Packet0CPlayerLook;
import me.galaran.mcsniffer.packets.Packet0DPlayerPositionLook;
import me.galaran.mcsniffer.packets.Packet33MapChunk;
import me.galaran.mcsniffer.packets.Packet35BlockChange;
import me.galaran.mcsniffer.packets.Packet82UpdateSign;

/**
 * Packet format: http://mc.kev009.com/Protocol
 * @author Galaran
 */
class TrafficAnalyzer {
    
    private static final Logger log = Logger.getLogger("galaran.diamf.diamond_finder");
    private final String analyzerType; // use for logging prefix
    
    private final PacketHandler proc;
    private final Map<Byte, Class<? extends Packet>> targetPackets;
    private static final int MAIN_BUFFER_SIZE = 24 * 1024;
    
    private ByteBuffer mainBuffer = ByteBuffer.allocate(MAIN_BUFFER_SIZE);

    public TrafficAnalyzer(PacketHandler proc, boolean isServerTrafficAnalyzer) {
        this.proc = proc;
        this.targetPackets = (isServerTrafficAnalyzer ? serverPackets : clientPackets);
        this.analyzerType = (isServerTrafficAnalyzer ? "[S] " : "[C] ");
    }
    
    public void handle(PcapPacketWrapper packet) {
        if (packet.isEmpty())
            return;
        
        log.log(Level.FINE, analyzerType + "Incoming data chunk, size = " + packet.getPayload().length);
        mainBuffer.put(packet.getPayload());
        processBuffer();
    }
    
    private void processBuffer() {
        mainBuffer.flip();
        int remain;
        try {
            while (mainBuffer.position() < mainBuffer.limit() - 1) {
                Packet probablyPacket = scanAndShiftPos();
                if (probablyPacket != null) {
                    // cut used bytes with relative limit unchanged
                    remain = mainBuffer.remaining();
                    mainBuffer.compact();
                    mainBuffer.position(0);
                    mainBuffer.limit(remain);
                    
                    log.log(Level.INFO, analyzerType + "Complete packet {0}", probablyPacket.getClass().getName());
                    proc.processPacket(probablyPacket);
                }
            }
            mainBuffer.compact(); // cut trash bytes
            log.log(Level.FINE, analyzerType + "Buffer pos after compacting: " + mainBuffer.position());
        } catch (NeedMoreBytesException ex) {
            
            // need another fragment
            log.log(Level.INFO, analyzerType + "Need another fragment! Shift pos to " + mainBuffer.limit());
            
            // preparing to append bytes to this buffer
            mainBuffer.position(mainBuffer.limit());
            mainBuffer.limit(mainBuffer.capacity());
        }
    }

    /**
     * Try to build packet with current buffer position
     * Recieves buffer with pos = scanning begin
     * Shifting:
     *  -- Not possible to create packet (ret null) - shift 1 byte
     *  -- Not enought bytes(NeedMoreBytesException) - first pos(not changed)
     *  -- Packet creation success(ret not null) - first byte after created packet
     *  -- Error creating packet - shift 1 byte (threats as not possible to create packet)
     * @return packet or null, if not possible
     * @throws NeedMoreBytesException begin of packet is valid, but not enought bytes
     */
    public Packet scanAndShiftPos() throws NeedMoreBytesException {
        mainBuffer.mark();
        try {
            if (!findAndValidate(mainBuffer)) {
                // pos was shifted arbitrarily
                mainBuffer.reset();
                mainBuffer.get();
                return null;
            }
        } catch (BufferUnderflowException ex) {
            mainBuffer.reset();
            throw new NeedMoreBytesException();
        }
    
        // its valid, build packet
        mainBuffer.reset();
        Packet newPacket;
        byte code = mainBuffer.get();
        try {
            newPacket = targetPackets.get(code).newInstance();
        } catch (Exception ex) {
            log.log(Level.SEVERE, analyzerType + "Error while building packet instance");
            // pos is already shifted here
            return null;
        }
        
        try {
            newPacket.readPacket(mainBuffer);
            newPacket.code = code;
        } catch (Exception ex) {
            log.log(Level.SEVERE,  analyzerType + "Error building packet " + newPacket.getClass().getName());
            
            newPacket = null;
            // change pos back to packet code + 1
            mainBuffer.reset();
            mainBuffer.get();
        }
        return newPacket;
    }
    
    private boolean findAndValidate(ByteBuffer buff) throws BufferUnderflowException {
        byte packetCode = buff.get();
        if (targetPackets.get(packetCode) == null) // no packet
            return false;
        
        PacketValidator val = validators.get(packetCode);
        return val.validate(buff);
    }
    
    private final static Map<Byte, Class<? extends Packet>> serverPackets = new HashMap<Byte, Class<? extends Packet>>();
    private final static Map<Byte, Class<? extends Packet>> clientPackets = new HashMap<Byte, Class<? extends Packet>>();
    private final static Map<Byte, Class<? extends Packet>> allPackets = new HashMap<Byte, Class<? extends Packet>>();
    
    private final static Map<Byte, PacketValidator> validators = new HashMap<Byte, PacketValidator>();
    
    static {
        serverPackets.put((byte)0x33, Packet33MapChunk.class);
        serverPackets.put((byte)0x35, Packet35BlockChange.class);
        serverPackets.put((byte)0x82, Packet82UpdateSign.class);
        
        clientPackets.put((byte)0x0B, Packet0BPlayerPosition.class);
        clientPackets.put((byte)0x0C, Packet0CPlayerLook.class);
        clientPackets.put((byte)0x0D, Packet0DPlayerPositionLook.class);
        
        allPackets.putAll(serverPackets);
        allPackets.putAll(clientPackets);
        
        for (Map.Entry<Byte, Class<? extends Packet>> entry : allPackets.entrySet()) {
            try {
                validators.put(entry.getKey(), (PacketValidator) entry.getValue().newInstance());
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Error while constructing validators map");
                ex.printStackTrace();
            }
        }
        
    }
    
    public static Map<Byte, Class<? extends Packet>> getPacketMap() {
        return allPackets;
    }
    
}
