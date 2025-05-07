package model.pieces;

import model.board.Posn;

public interface Piece {
    String getName();

    Posn getPosn();

    void setPosn(Posn posn);

    String getResourcePath();
}
