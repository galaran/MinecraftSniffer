package me.galaran.mcsniffer.util;

public class Coord {
    public static int MAX_X_ABS = 100000;
    public static int MAX_Z_ABS = 100000;
    public static int PLAYER_MIN_Y = -20000;
    public static int PLAYER_MAX_Y = 200;
    public static int BLOCK_MAX_Y_ABS = 130;
    public static float MAX_YAW_ABS = 10000000;
    public static float MAX_PITCH_ABS = 90;
    
    public int x;
    public int y;
    public int z;

    public Coord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coord other = (Coord) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", z=" + z;
    }
    
    
    
}
