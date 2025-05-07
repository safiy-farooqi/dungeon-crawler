package model.pieces;

public class Enemy extends APiece implements MovablePiece {

    public Enemy() {
        super("Enemy", "images/enemy.png");
    }

    @Override
    public CollisionResult collide(Piece other) {
        if (other == null) {
            return new CollisionResult(0, CollisionResult.Result.CONTINUE);
        }
        if (other instanceof Treasure) {
            return new CollisionResult(0, CollisionResult.Result.CONTINUE);
        }
        if (other instanceof Hero) {
            return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
        }
        if (other instanceof Exit) {
            throw new IllegalArgumentException("Enemy cannot collide with exit");
        }
        if (other instanceof Wall) {
            throw new IllegalArgumentException("Enemy cannot collide with wall");
        }
        throw new IllegalArgumentException("Enemy collided with invalid piece");
    }
}
