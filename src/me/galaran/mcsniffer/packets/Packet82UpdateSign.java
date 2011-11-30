package me.galaran.mcsniffer.packets;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.galaran.mcsniffer.util.Vec3D;

public class Packet82UpdateSign extends Packet {

    private static final Logger log = Logger.getLogger("galaran.diamf.sniffer");
    //private int x; //1
    //private short y; //5
    //private int z; //7
    public String[] signStrings;
    public Vec3D coord;

    @Override
    public void readPacket(ByteBuffer buff) throws Exception {
        coord = new Vec3D(buff.getInt(), buff.getShort(), buff.getInt());
        
        short len;
        String curStr;
        byte[] rawString;
        signStrings = new String[4];
        try {
            for (int i = 0; i < 4; i++) {
                len = buff.getShort();
                if (len == 0) {
                    curStr = "";
                } else {
                    rawString = new byte[len * 2]; // 2 bytes per symbol
                    buff.get(rawString);
                    curStr = new String(rawString, "UTF-16BE");
                }
                signStrings[i] = curStr;
            }
        } catch (UnsupportedEncodingException ex) {
            log.log(Level.SEVERE, "Error decoding sign String");
        }
    }

    @Override
    public boolean validate(ByteBuffer buff) throws BufferUnderflowException {
        
        if (Math.abs(buff.getInt()) > Vec3D.MAX_X_ABS) {
            return false; // block x must be in interval [-MAX_X_ABS; +MAX_X_ABS]
        }
        
        if (Math.abs(buff.getShort()) > Vec3D.BLOCK_MAX_Y_ABS) {
            return false; // block y must be in interval [-BLOCK_MAX_Y_ABS; +BLOCK_MAX_Y_ABS]
        }
        if (Math.abs(buff.getInt()) > Vec3D.MAX_Z_ABS) {
            return false; // block z must be in interval [-MAX_Z_ABS; +MAX_Z_ABS]
        }
        
        //check Strings
        short len;
        byte[] rawString;
        try {
            for (int i = 0; i < 4; i++) {
                len = buff.getShort();
                if (len < 0 || len > 20)
                    return false;
                if (len != 0) {
                    rawString = new byte[len * 2]; // 2 bytes per symbol
                    buff.get(rawString);
                    new String(rawString, "UTF-16BE");
                }
            }
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
        
        return true;
    }
}
