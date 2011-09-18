package me.galaran.mcsniffer.util;

public class Block {
    public int id;
    public Coord coord;

    public Block(Coord coord, int id) {
        this.id = id;
        this.coord = coord;
    }
    
    public Block(int x, int y, int z, int id) {
        this(new Coord(x, y, z), id);
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + this.id;
        hash = 41 * hash + (this.coord != null ? this.coord.hashCode() : 0);
        return hash;
    }
}
