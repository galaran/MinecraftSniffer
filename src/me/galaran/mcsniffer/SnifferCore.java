package me.galaran.mcsniffer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

class SnifferCore {
    private static final Logger log = Logger.getLogger("galaran.diamf.diamond_finder");

    private final String SERVER_NAME;
    private final int IF_NUM;
    String SERVER_IP;

    private final PacketHandler proc;
    private final TrafficAnalyzer serverPacketsAnalyzer;
    private final TrafficAnalyzer clientPacketsAnalyzer;
    
    private Pcap pcap = null;
    private List<PcapIf> ifs = null;
    private PcapPacketHandler catcher = null;

    public SnifferCore(String host, int ifNum, PacketHandler proc) {
        this.SERVER_NAME = host;
        this.IF_NUM = ifNum;
        this.proc = proc;

        configureLogging();
        
        // init
        serverPacketsAnalyzer = new TrafficAnalyzer(this.proc, true);
        clientPacketsAnalyzer = new TrafficAnalyzer(this.proc, false);
        
        // resolve server and local IP
        try {
            SERVER_IP = Inet4Address.getByName(SERVER_NAME).getHostAddress();
        } catch (UnknownHostException ex) {
            log.log(Level.INFO, "Coudnt resolve hostname {0}", SERVER_NAME);
            System.exit(1);
        }
        
        StringBuilder errbuf = new StringBuilder();
        ifs = new ArrayList<PcapIf>(); // Will hold list of devices
        int statusCode = Pcap.findAllDevs(ifs, errbuf);
        if (statusCode != Pcap.OK) {
            log.log(Level.INFO, "Error occured: {0}", errbuf);
            System.exit(1);
        }
        
        System.out.println("Inteface list:");
        for (int i = 0; i < ifs.size(); i++) {
            System.out.println("#" + i + ": " + ifs.get(i).getDescription());
        }

        PcapIf netInterface = ifs.get(IF_NUM);

        // open session
        int snaplen = 2048; // Truncate packet at this size
        int promiscous = Pcap.MODE_PROMISCUOUS;
        int timeout = 60 * 1000; // In milliseconds
        pcap = Pcap.openLive(netInterface.getName(), snaplen, promiscous, timeout, errbuf);

        //filter
        PcapBpfProgram filter = new PcapBpfProgram();
        String expression = "host " +  SERVER_IP;
        int optimize = 0; // 1 means true, 0 means false
        int netmask = 0;

        int r = pcap.compile(filter, expression, optimize, netmask);
        if (r != Pcap.OK) {
            System.out.println("Filter error: " + pcap.getErr());
            System.exit(1);
        }
        pcap.setFilter(filter);
        
        catcher = new PcapPacketHandler() {

            @Override
            public void nextPacket(PcapPacket packet, Object user) {
                PcapPacketWrapper myPacket = new PcapPacketWrapper(packet, SERVER_IP);
                if (myPacket.isServerPacket())
                    serverPacketsAnalyzer.handle(myPacket);
                else
                    clientPacketsAnalyzer.handle(myPacket);
            }
        };
}
    
    @SuppressWarnings("unchecked")
    public void sniffLoop() {
        int count = 0; // Capture packet count
        System.out.println("\nStart listerning at " + SERVER_IP + " on if " + ifs.get(IF_NUM).getDescription());
        pcap.loop(count, catcher, null);
        pcap.close();
    }

    private void configureLogging() {        
        Handler mainHandler = null;
        try {
            mainHandler = new FileHandler("diamond_finder.log");
        } catch (IOException ex) {
            System.err.println("coudn't configure logging");
            return;
        }
        mainHandler.setFormatter(new Formatter() {

            @Override
            public String format(LogRecord record) {
                StringBuilder buf = new StringBuilder();
                buf.append(record.getLevel());
                buf.append(": ");
                buf.append(formatMessage(record));
                buf.append('\n');
                return buf.toString();
            }
        });
        
        Logger.getLogger("galaran.diamf").setUseParentHandlers(false);
        Logger.getLogger("galaran.diamf.diamond_finder").addHandler(mainHandler);
    }
    
}
