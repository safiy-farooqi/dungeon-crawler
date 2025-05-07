package view;


import controller.Controller;
import model.Model;
import model.Model.STATUS;
import model.Observer;
import model.board.BoardImpl;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TitleScreenView implements FXComponent, Observer {
    private final Controller controller;
    private final Model model;
    private final Stage stage;
    private Parent root;

    public TitleScreenView(Controller controller, Model model, Stage stage) {
        this.controller = controller;
        this.model      = model;
        this.stage      = stage;
        build();
    }

    private void build() {
        StackPane pane = new StackPane();
        pane.getStyleClass().setAll("title-screen");

        // Title / Scores
        Label title     = new Label("DUNGEON CRAWLER");
        title.getStyleClass().add("title-label");
        Label hiLabel   = new Label("High Score: " + model.getHighScore());
        hiLabel.getStyleClass().add("scoreboard-label");
        Label lastLabel = new Label("Last Score: " + model.getCurScore());
        lastLabel.getStyleClass().add("scoreboard-label");

        // ── new: hard/easy toggle ──
        CheckBox hardToggle = new CheckBox("Hard Mode");
        hardToggle.setSelected(false);
        hardToggle.getStyleClass().add("scoreboard-label");

        // Start button
        Button start = new Button("▶ Start Game");
        start.getStyleClass().add("title-button");
        start.setOnAction(e -> {
            boolean hard = hardToggle.isSelected();
            BoardImpl.setHardMode(hard);
            controller.startGame();
            GameView gv = new GameView(controller, model, stage);
            model.addObserver(gv);
            Parent rootNode = gv.render();
            stage.getScene().setRoot(rootNode);
            rootNode.requestFocus();          // ← now in *both* modes
        });
        Label footer = new Label("By Safiy Farooqi");
        footer.getStyleClass().add("footer-label");

        // Layout
        VBox box = new VBox(16,
                title, hiLabel, lastLabel,
                hardToggle,      // <─ checkbox here
                start, footer
        );
        box.setAlignment(Pos.CENTER);
        pane.getChildren().setAll(box);
        this.root = pane;
    }

    @Override
    public Parent render() {
        build();  // refresh high/last scores and toggle state
        return root;
    }

    @Override
    public void update() {
        if (model.getStatus() == STATUS.END_GAME) {
            // when model flips to END_GAME, re-show this screen
            stage.getScene().setRoot(render());
        }
    }
}