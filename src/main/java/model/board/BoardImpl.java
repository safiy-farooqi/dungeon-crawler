package model.board;

import model.pieces.*;
import java.util.*;

public class BoardImpl implements Board {
    // ──────── MODE SWITCH ────────
    private static boolean hardMode = false;
    /** Call this _once_ (from TitleScreenView) before startGame() */
    public static void setHardMode(boolean on) {
        hardMode = on;
    }
    // ─────────────────────────────

    private final int width;
    private final int height;
    private final Piece[][] board;
    private Hero hero;
    private Exit exit;
    private final List<Enemy> enemies = new ArrayList<>();

    public BoardImpl(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.board = new Piece[height][width];
    }

    public BoardImpl(Piece[][] array) {
        if (array == null || array.length == 0 || array[0].length == 0) {
            throw new IllegalArgumentException("Board array can't be null or empty");
        }
        this.height = array.length;
        this.width = array[0].length;
        this.board = new Piece[height][width];
        for (int r = 0; r < height; r++) {
            if (array[r].length != width) {
                throw new IllegalArgumentException("Uneven board array");
            }
            for (int c = 0; c < width; c++) {
                board[r][c] = array[r][c];
                if (board[r][c] instanceof Hero) {
                    hero = (Hero) board[r][c];
                    hero.setPosn(new Posn(r, c));
                } else if (board[r][c] instanceof Exit) {
                    exit = (Exit) board[r][c];
                    exit.setPosn(new Posn(r, c));
                } else if (board[r][c] instanceof Enemy) {
                    Enemy e = (Enemy) board[r][c];
                    e.setPosn(new Posn(r, c));
                    enemies.add(e);
                }
            }
        }
    }

    @Override
    public void init(int numEnemies, int numTreasures, int numWalls) {
        // clear old state
        this.hero = new Hero();
        this.exit = new Exit();
        this.enemies.clear();
        List<Posn> empty = new ArrayList<>();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                board[r][c] = null;
                empty.add(new Posn(r, c));
            }
        }
        int totalSpots = width * height;
        int needed = numEnemies + numTreasures + numWalls + 2;
        if (needed > totalSpots) {
            throw new IllegalArgumentException(
                    "Not enough space: need " + needed + ", have " + totalSpots);
        }
        Collections.shuffle(empty);
        Iterator<Posn> it = empty.iterator();
        // place hero
        Posn p = it.next();
        board[p.getRow()][p.getCol()] = hero;
        hero.setPosn(p);
        // place exit
        p = it.next();
        board[p.getRow()][p.getCol()] = exit;
        exit.setPosn(p);
        // enemies
        for (int i = 0; i < numEnemies; i++) {
            Enemy e = new Enemy();
            p = it.next();
            board[p.getRow()][p.getCol()] = e;
            e.setPosn(p);
            enemies.add(e);
        }
        // treasures
        for (int i = 0; i < numTreasures; i++) {
            Treasure t = new Treasure();
            p = it.next();
            board[p.getRow()][p.getCol()] = t;
            t.setPosn(p);
        }
        // walls
        for (int i = 0; i < numWalls; i++) {
            Wall w = new Wall();
            p = it.next();
            board[p.getRow()][p.getCol()] = w;
            w.setPosn(p);
        }
    }

    @Override public int getWidth()  { return width; }
    @Override public int getHeight() { return height; }

    @Override
    public Piece get(Posn posn) {
        if (posn == null) throw new IllegalArgumentException("Posn cannot be null");
        int r = posn.getRow(), c = posn.getCol();
        if (r < 0 || r >= height || c < 0 || c >= width) {
            throw new IllegalArgumentException("Posn out of bounds");
        }
        return board[r][c];
    }

    @Override
    public void set(Piece p, Posn newPos) {
        if (p == null || newPos == null) {
            throw new IllegalArgumentException("Piece and Posn must be non-null");
        }
        int r = newPos.getRow(), c = newPos.getCol();
        if (r < 0 || r >= height || c < 0 || c >= width) {
            throw new IllegalArgumentException("Posn out of bounds");
        }
        board[r][c] = p;
        p.setPosn(newPos);
        if (p instanceof Hero)      this.hero = (Hero) p;
        else if (p instanceof Enemy) this.enemies.add((Enemy) p);
        else if (p instanceof Exit)  this.exit = (Exit) p;
    }

    @Override
    public CollisionResult moveHero(int drow, int dcol) {
        Posn cur = hero.getPosn();
        Posn dest = new Posn(cur.getRow() + drow, cur.getCol() + dcol);
        // illegal? off‐board or wall
        if (dest.getRow() < 0 || dest.getRow() >= height
                || dest.getCol() < 0 || dest.getCol() >= width
                || get(dest) instanceof Wall) {
            return new CollisionResult(0, CollisionResult.Result.CONTINUE);
        }
        // move hero
        board[cur.getRow()][cur.getCol()] = null;
        Piece collided = board[dest.getRow()][dest.getCol()];
        board[dest.getRow()][dest.getCol()] = hero;
        hero.setPosn(dest);
        // hero collide logic
        CollisionResult res = hero.collide(collided);
        if (res.getResults() != CollisionResult.Result.CONTINUE) {
            return res;  // GAME_OVER or NEXT_LEVEL
        }
        // now move enemies _once_
        moveAllEnemies();
        // if any enemy landed on hero
        for (Enemy e : enemies) {
            if (e.getPosn().equals(hero.getPosn())) {
                return new CollisionResult(res.getPoints(), CollisionResult.Result.GAME_OVER);
            }
        }
        return res;  // CONTINUE + possibly points
    }

    // ──────────── enemy turns ────────────
    private void moveAllEnemies() {
        Collections.shuffle(enemies);
        for (Enemy e : enemies) {
            moveOneEnemy(e);
        }
    }

    private void moveOneEnemy(Enemy e) {
        if (hardMode) chaseHero(e);
        else          randomMove(e);
    }

    // Part 1 random
    private void randomMove(Enemy e) {
        List<Posn> dirs = new ArrayList<>(List.of(
                new Posn(-1,0), new Posn(1,0),
                new Posn(0,-1), new Posn(0,1)
        ));
        Collections.shuffle(dirs);
        for (Posn d : dirs) {
            Posn next = new Posn(
                    e.getPosn().getRow() + d.getRow(),
                    e.getPosn().getCol() + d.getCol()
            );
            if (!isBlocked(next)) {
                shiftEnemy(e, next);
                return;
            }
        }
    }

    // chase the hero along whichever axis has greater delta
    private void chaseHero(Enemy e) {
        Posn cur = e.getPosn();
        Posn hp  = hero.getPosn();
        int dr = hp.getRow() - cur.getRow();
        int dc = hp.getCol() - cur.getCol();
        Posn primary   = Math.abs(dr) >= Math.abs(dc)
                ? new Posn(Integer.signum(dr), 0)
                : new Posn(0, Integer.signum(dc));
        Posn secondary = Math.abs(dr) >= Math.abs(dc)
                ? new Posn(0, Integer.signum(dc))
                : new Posn(Integer.signum(dr), 0);
        for (Posn d : List.of(primary, secondary)) {
            Posn next = new Posn(cur.getRow() + d.getRow(),
                    cur.getCol() + d.getCol());
            if (!isBlocked(next)) {
                shiftEnemy(e, next);
                return;
            }
        }
        // otherwise skip turn
    }

    private boolean isBlocked(Posn p) {
        if (p.getRow() < 0 || p.getRow() >= height
                || p.getCol() < 0 || p.getCol() >= width) return true;
        Piece occ = board[p.getRow()][p.getCol()];
        return (occ instanceof Wall)
                || (occ instanceof Exit)
                || (occ instanceof Enemy);
    }

    private void shiftEnemy(Enemy e, Posn next) {
        Posn cur = e.getPosn();
        board[cur.getRow()][cur.getCol()] = null;
        board[next.getRow()][next.getCol()] = e;
        e.setPosn(next);
    }
}