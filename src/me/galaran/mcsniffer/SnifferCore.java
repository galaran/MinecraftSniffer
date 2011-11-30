package me.galaran.mcsniffer;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

class SnifferCore {
    private static final Logger log = Logger.getLogger("galaran.diamf.sniffer");

    private final String HOST;
    private final int IF_NUM;
    private byte[] SERVER_IP;

    private final PacketHandler proc;
    private final TrafficAnalyzer serverPacketsAnalyzer;
    private final TrafficAnalyzer clientPacketsAnalyzer;
    
    private final Pcap pcap;
    private final List<PcapIf> ifs;
    private final PcapPacketHandler catcher;
    private final Ip4 ip4 = new Ip4();
    private final Tcp tcp = new Tcp();

    SnifferCore(String host, int ifNum, PacketHandler proc) {
        this.HOST = host;
        this.IF_NUM = ifNum;
        this.proc = proc;

        configureLogging();
        
        // init
        serverPacketsAnalyzer = new TrafficAnalyzer(this.proc, true);
        clientPacketsAnalyzer = new TrafficAnalyzer(this.proc, false);
        
        // resolve server and local IP
        try {
            SERVER_IP = Inet4Address.getByName(HOST).getAddress();
        } catch (UnknownHostException ex) {
            log.log(Level.INFO, "Coudnt resolve hostname {0}", HOST);
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
        String expression = "ip and tcp and host " + FormatUtils.ip(SERVER_IP);
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
                
                boolean isServerPacket = false;
                packet.getHeader(ip4);
                if (Arrays.equals(ip4.source(), SERVER_IP)) {
                    isServerPacket = true;
                }

                packet.getHeader(tcp);
                byte[] payload;
                if (tcp.getPayloadLength() > 0) {
                    payload = tcp.getPayload();
                } else {
                    return; // ignore this packet
                }
                
                // process packet
                if (isServerPacket) {
                    serverPacketsAnalyzer.handle(payload);
                } else {
                    clientPacketsAnalyzer.handle(payload);
                }
            }
        };
}
    
    @SuppressWarnings("unchecked")
    public void sniffLoop() {
        int count = 0; // Capture packet count
        System.out.println("\nStart listerning at " + FormatUtils.ip(SERVER_IP) + " on if " + ifs.get(IF_NUM).getDescription());
        pcap.loop(count, catcher, null);
        pcap.close();
    }

    private void configureLogging() {        
        Logger.getLogger("galaran.diamf").setUseParentHandlers(false);
    }
}
