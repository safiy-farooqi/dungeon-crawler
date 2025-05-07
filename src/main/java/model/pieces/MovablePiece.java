package model.pieces;

public interface MovablePiece extends Piece {
    CollisionResult collide(Piece other);
}
