package controller;

import model.*;
import java.util.Objects;

public class ControllerImpl implements Controller {
    private final Model model;

    public ControllerImpl(Model model) {
        this.model = Objects.requireNonNull(model, "Model cannot be null");
    }

    @Override
    public void startGame() {
        model.startGame();
    }

    @Override
    public void moveUp() {
        model.moveUp();
    }

    @Override
    public void moveDown() {
        model.moveDown();
    }

    @Override
    public void moveLeft() {
        model.moveLeft();
    }

    @Override
    public void moveRight() {
        model.moveRight();
    }
}