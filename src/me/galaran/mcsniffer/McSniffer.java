package me.galaran.mcsniffer;

public class McSniffer {
    
    private static McSniffer instance = null;
    
    private PacketProcessor proc; 
    private SnifferCore core;

    private McSniffer() {
    }
    
    public synchronized static McSniffer getInstance() {
        if (instance == null)
            instance = new McSniffer();
        return instance;
    }

    public void registerHandler(byte code, PacketHandler handler) {
        proc.registerHandler(code, handler);
    }

    public void sniffLoop() {
        core.sniffLoop();
    }

    public void init(int ifNum, String server) {
        proc = new PacketProcessor();
        core = new SnifferCore(server, ifNum, proc);
    }
}
