package model;

import model.board.Board;
import model.board.BoardImpl;
import model.board.Posn;
import model.pieces.CollisionResult;
import model.pieces.Piece;
import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {
    private final Board board;
    private int curScore;
    private int highScore;
    private STATUS status;
    private int level;
    private final List<Observer> observers;

    public ModelImpl(int width, int height) {
        this.board = new BoardImpl(width, height);
        this.curScore = 0;
        this.highScore = 0;
        this.status = STATUS.END_GAME;
        this.level = 0;
        this.observers = new ArrayList<>();
    }

    public ModelImpl(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board can't be null");
        }
        this.board = board;
        this.curScore = 0;
        this.highScore = 0;
        this.status = STATUS.END_GAME;
        this.level = 0;
        this.observers = new ArrayList<>();
    }

    @Override
    public int getWidth() {
        return board.getWidth();
    }

    @Override
    public int getHeight() {
        return board.getHeight();
    }

    @Override
    public Piece get(Posn p) {
        return board.get(p);
    }

    @Override
    public int getCurScore() {
        return curScore;
    }

    @Override
    public int getHighScore() {
        return highScore;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public STATUS getStatus() {
        return status;
    }

    @Override
    public void startGame() {
        this.curScore = 0;
        this.level = 1;
        this.status = STATUS.IN_PROGRESS;
        board.init(this.level + 1, 3, 3);
        notifyObservers();
    }

    @Override
    public void endGame() {
        this.status = STATUS.END_GAME;
        if (this.curScore > this.highScore) {
            highScore = curScore;
        }
        notifyObservers();
    }

    @Override
    public void moveUp() {
        CollisionResult cr = board.moveHero(-1, 0);
        newScore(cr);
    }

    private void newScore(CollisionResult cr) {
        curScore += cr.getPoints();
        switch (cr.getResults()) {
            case GAME_OVER:
                endGame();
                break;
            case NEXT_LEVEL:
                level++;
                board.init(level + 1, 3, 3);
                notifyObservers();
                break;
            case CONTINUE:
                notifyObservers();
                break;
        }
    }

    @Override
    public void moveDown() {
        CollisionResult cr = board.moveHero(1, 0);
        newScore(cr);
    }

    @Override
    public void moveLeft() {
        CollisionResult cr = board.moveHero(0, -1);
        newScore(cr);
    }

    @Override
    public void moveRight() {
        CollisionResult cr = board.moveHero(0, 1);
        newScore(cr);
    }

    @Override
    public void addObserver(Observer o) {
        if (o == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        observers.add(o);
    }

    // --- helper to broadcast to your Subject observers ---
    private void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }
}
