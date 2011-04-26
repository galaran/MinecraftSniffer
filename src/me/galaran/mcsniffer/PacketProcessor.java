package me.galaran.mcsniffer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import me.galaran.mcsniffer.PacketHandler;

import me.galaran.mcsniffer.packets.Packet;
import me.galaran.mcsniffer.packets.Packet0BPlayerPosition;
import me.galaran.mcsniffer.packets.Packet0CPlayerLook;
import me.galaran.mcsniffer.packets.Packet0DPlayerPositionLook;
import me.galaran.mcsniffer.packets.Packet33MapChunk;
import me.galaran.mcsniffer.packets.Packet35BlockChange;

class PacketProcessor {
    
//    private final Object diamondListLock = new Object();
    private static final Logger log = Logger.getLogger("galaran.diamf.diamond_finder");
//    private static final Logger diamondWriter = Logger.getLogger("galaran.diamf.diamonds");
//    Set<Block> diamonds = new HashSet<Block>();
    
//    ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
    
    private Map<Byte, PacketHandler> handlers = new HashMap<Byte, PacketHandler>();
    
//    {
//        ex.scheduleWithFixedDelay(new SimpleMapCreator(), 20, 20, TimeUnit.SECONDS);
//    }
    
    public synchronized void registerHandler(byte packetCode, PacketHandler handler) {
        handlers.put(packetCode, handler);
    } 
    
    public void processPacket(Packet packet) {
        log.log(Level.INFO, "Complete packet {0}", packet.getClass().getName());
        PacketHandler handlerForThis = handlers.get(packet.code);
        if (handlerForThis != null)
            handlerForThis.handlePacket(packet);
        
//        if (packet instanceof Packet33MapChunk)
//            processPacket33((Packet33MapChunk) packet);
//        else if (packet instanceof Packet35BlockChange)
//            processPacket35((Packet35BlockChange) packet);
//        else if (packet instanceof Packet0BPlayerPosition)
//            processPacket0B((Packet0BPlayerPosition) packet);
//        else if (packet instanceof Packet0CPlayerLook)
//            processPacket0C((Packet0CPlayerLook) packet);
//        else if (packet instanceof Packet0DPlayerPositionLook)
//            processPacket0D((Packet0DPlayerPositionLook) packet);
    }
    
//    private void processPacket33(Packet33MapChunk packet) {
//        for (Block cur : packet.blocks) {
//            if (cur.id == 56) {
//                synchronized (diamondListLock) {
//                    diamonds.add(cur);
//                }
//                System.out.println(cur);
//                diamondWriter.info(cur.toString());
//            }
//        }
//    }
//
//    private void processPacket35(Packet35BlockChange packet) {
//        //System.out.println("Block Changed: " + packet.blockChange.toString());
//    }
//
//    private void processPacket0B(Packet0BPlayerPosition packet) {
//        System.out.println("Player moved to: " + packet.playerCoords.toString());
//    }
//
//    private void processPacket0C(Packet0CPlayerLook packet) {
//        System.out.println("Player looks " + packet.yaw + ", " + packet.pitch);
//    }
//
//    private void processPacket0D(Packet0DPlayerPositionLook packet) {
//        System.out.println("Player moved to: " + packet.playerCoords.toString());
//        System.out.println("Player looks " + packet.yaw + ", " + packet.pitch);
//    }
    
//    class SimpleMapCreator implements Runnable {
//
//        @Override
//        public void run() {
//            createSimpleMap();
//        }
//        
//        void createSimpleMap() {
//            long begin = System.currentTimeMillis();
//            BufferedImage image = new BufferedImage(3600, 3600, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2d = image.createGraphics();
//
//            g2d.setColor(Color.BLACK);
//            g2d.fillRect(0, 0, 3600, 3600);
//            g2d.setColor(Color.WHITE);
//            g2d.drawLine(1800, 0, 1800, 3599);
//            g2d.drawLine(0, 1800, 3599, 1800);
//            g2d.drawRect(0, 0, 3599, 3599);
//
//            List<Block> diamondsCopy = null;
//            synchronized (diamondListLock) {
//                diamondsCopy = new ArrayList<Block>(diamonds);
//            }
//            if (!diamondsCopy.isEmpty()) {
//                for (Block curBlock : diamondsCopy) {
//                    int curImgX = curBlock.coord.x + 1800;
//                    int curImgZ = -curBlock.coord.z + 1800;
//                    int curColor = image.getRGB(curImgX, curImgZ);
//                    if (curColor == 0xFF000000) { // if black
//                        g2d.setColor(new Color(0xFF4CFF00)); // green
//                        g2d.fillOval(curImgX, curImgZ, 4, 4);
//                    } else {
//                        if (curColor == 0xFF4CFF00) { // if green
//                            g2d.setColor(new Color(0xFF0026FF)); // blue
//                            g2d.fillOval(curImgX, curImgZ, 4, 4);
//                        } else {
//                            if (curColor == 0xFF0026FF) { // if blue
//                                g2d.setColor(new Color(0xFF6B3F7F)); // epic!
//                                g2d.fillOval(curImgX, curImgZ, 4, 4);
//                            }
//                        }
//                    }
//                } // for
//            } // if
//            long dur = System.currentTimeMillis() - begin;
//            log.info("Rendered image [" + diamondsCopy.size() + "], time: " + dur + "ms");
//
//            try {
//                File imageFile = new File("simplemap.png");
//                ImageIO.write(image, "png", imageFile);
//            } catch (IOException ex) {
//                log.info("Error saving rendered simple map");
//            }
//        } // createSimpleMap()
//        
//    } // class SimpleMapCreator

}
