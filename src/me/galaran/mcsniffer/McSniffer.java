package me.galaran.mcsniffer;

public class McSniffer {
    
    private static McSniffer instance = null;
    
    private PacketHandler packetHandler; 
    private SnifferCore core;

    private McSniffer() {
    }
    
    public synchronized static McSniffer getInstance() {
        if (instance == null)
            instance = new McSniffer();
        return instance;
    }

    /**
     * Only registered processor per packet code
     */
    public void registerPacketProcessor(byte code, PacketProcessor ph) {
        packetHandler.registerHandler(code, ph);
    }
    
    /**
     * Only registered processor per application
     */
    public void registerChunkProcessor(ChunkProcessor ch) {
        packetHandler.registerChunkHandler(ch);
    }

    public void startSniff() {
        core.sniffLoop();
    }

    public void init(int ifNum, String server) {
        packetHandler = new PacketHandler();
        core = new SnifferCore(server, ifNum, packetHandler);
    }
}
