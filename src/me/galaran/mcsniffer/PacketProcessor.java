package me.galaran.mcsniffer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.galaran.mcsniffer.packets.Packet;

class PacketProcessor {
    
    private static final Logger log = Logger.getLogger("galaran.diamf.diamond_finder");
    private Map<Byte, PacketHandler> handlers = new HashMap<Byte, PacketHandler>();
    
    public synchronized void registerHandler(byte packetCode, PacketHandler handler) {
        handlers.put(packetCode, handler);
    } 
    
    public void processPacket(Packet packet) {
        PacketHandler handlerForThis = handlers.get(packet.code);
        if (handlerForThis != null)
            handlerForThis.handlePacket(packet);
    }

}
