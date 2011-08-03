package me.galaran.mcsniffer.util;

public class Block {
    public int id;
    public Coord coord;

    public Block(int x, int y, int z, int id) {
        coord = new Coord(x, y, z);
        this.id = id;
    }

    @Override
    public String toString() {
        return "Block" + coord + ": " + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Block other = (Block) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.coord != other.coord && (this.coord == null || !this.coord.equals(other.coord))) {
            return false;
        }
        return true;
    }
    
}
