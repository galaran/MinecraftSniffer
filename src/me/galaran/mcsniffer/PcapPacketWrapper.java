package me.galaran.mcsniffer;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

class PcapPacketWrapper {
    
    private byte[] payload;
    private boolean isEmpty = true;
    private boolean isServerPacket = false;

    PcapPacketWrapper(PcapPacket nativePacket, String serverIp) {
        Ip4 ip = new Ip4();
        if (nativePacket.hasHeader(ip)) {
            byte[] ipAddr = ip.source();
            if (serverIp.equals(FormatUtils.ip(ipAddr)))
                isServerPacket = true;
            
            Tcp tcp = new Tcp();
            if (nativePacket.hasHeader(tcp)) {
                if (tcp.getPayloadLength() > 0) {
                    payload = tcp.getPayload();
                    isEmpty = false;
                }
            }
        }
        
    }

    public byte[] getPayload() {
        return payload;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isServerPacket() {
        return isServerPacket;
    }
    
}
