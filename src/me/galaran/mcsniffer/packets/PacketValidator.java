package me.galaran.mcsniffer.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public interface PacketValidator {
    /**
     * @param buff Bytebuffer with pos on packet begin (after packet code).
     */
    boolean validate(ByteBuffer buff) throws BufferUnderflowException;
}
