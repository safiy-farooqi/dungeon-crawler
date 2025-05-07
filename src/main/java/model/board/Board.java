package model.board;

import model.pieces.CollisionResult;
import model.pieces.Piece;

/** Defines the public operations on the game board. */
public interface Board {

    void init(int enemies, int treasures, int walls);

    int getWidth();

    int getHeight();

    Piece get(Posn posn);

    void set(Piece p, Posn newPos);

    CollisionResult moveHero(int drow, int dcol);

}
