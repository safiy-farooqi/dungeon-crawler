package view;

import controller.Controller;
import controller.ControllerImpl;
import model.Model;
import model.ModelImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class AppLauncher extends Application {
    private static final int COLS = 15;
    private static final int ROWS = 10;
    private static final int CELL_SIZE = 68;

    private static final int EXTRA_HEIGHT = 150;

    @Override
    public void start(Stage stage) {
        // 1) MVC wiring
        Model model = new ModelImpl(COLS, ROWS);
        Controller controller = new ControllerImpl(model);

        // 2) start on the title screen
        TitleScreenView titleView = new TitleScreenView(controller, model, stage);
        model.addObserver(titleView);

        int sceneWidth = COLS * CELL_SIZE;
        int sceneHeight = ROWS * CELL_SIZE + EXTRA_HEIGHT;

        // 3) one Scene for everything
        Scene scene = new Scene(titleView.render(), sceneWidth, sceneHeight);
        scene.getStylesheets().add(getClass().getResource("/dungeon.css").toExternalForm());

        // 4) global arrow‐key handler
        scene.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();
            switch (k) {
                case UP    -> controller.moveUp();
                case DOWN  -> controller.moveDown();
                case LEFT  -> controller.moveLeft();
                case RIGHT -> controller.moveRight();
                default    -> { /* ignore */ }
            }
            // after any model change, re‐draw the current view:
            // either TitleScreenView or GameView, depending on status
            // (they both implement Observer.notify())
            // calling model.notifyObservers() indirectly happens inside moveX()/startGame()/endGame()
        });

        // 5) show
        stage.setTitle("Safiy's Dungeon Crawler");
        stage.setScene(scene);
        stage.show();
        stage.sizeToScene();

        // 6) make sure the root has focus so arrow‐keys will fire
        scene.getRoot().requestFocus();
    }
}