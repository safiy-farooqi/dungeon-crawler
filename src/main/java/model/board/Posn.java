package model.board;

public class Posn {
    private final int row;
    private final int col;

    public Posn(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return row + "," + col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Posn)) return false;
        Posn p = (Posn) o;
        return this.row == p.row && this.col == p.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
