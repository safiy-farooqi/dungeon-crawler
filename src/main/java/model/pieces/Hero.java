package model.pieces;

public class Hero extends APiece implements MovablePiece {

    public Hero() {
        super("Hero", "images/hero.png");
    }

    @Override
    public CollisionResult collide(Piece other) {
        if (other == null) {
            return new CollisionResult(0, CollisionResult.Result.CONTINUE);
        }
        if (other instanceof Treasure) {
            int points = ((Treasure) other).getValue();
            return new CollisionResult(points, CollisionResult.Result.CONTINUE);
        }
        if (other instanceof Enemy) {
            return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
        }
        if (other instanceof Exit) {
            return new CollisionResult(0, CollisionResult.Result.NEXT_LEVEL);
        }
        if (other instanceof Wall) {
            throw new IllegalArgumentException("Hero cannot collide with wall");
        }
        throw new IllegalArgumentException("Hero collided with invalid piece");
    }
}
