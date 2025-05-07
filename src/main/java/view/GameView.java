package view;

import controller.Controller;
import model.Model;
import model.Observer;
import model.board.Posn;
import model.pieces.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Objects;

public class GameView implements FXComponent, Observer {
    private final Controller controller;
    private final Model model;
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private final GridPane grid = new GridPane();
    private static final int CELL_SIZE = 68;

    private final Image heroImg =
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/hero.png")));
    private final Image enemyImg =
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/enemy.png")));
    private final Image treasureImg =
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/treasure.png")));
    private final Image exitImg =
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/exit.png")));
    private final Image wallImg =
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wall.png")));

    public GameView(Controller controller, Model model, Stage stage) {
        this.controller = controller;
        this.model = model;
        this.stage = stage;
        root.getStyleClass().add("game-background");
        build();
    }

    private void build() {
        /* --- board grid --- */
        grid.getStyleClass().setAll("grid-pane");
        grid.getChildren().clear();
        for (int r = 0; r < model.getHeight(); r++) {
            for (int c = 0; c < model.getWidth(); c++) {
                Posn p = new Posn(r, c);
                Piece pc = model.get(p);
                ImageView iv = new ImageView();
                iv.setFitWidth(CELL_SIZE);
                iv.setFitHeight(CELL_SIZE);
                iv.setPreserveRatio(true);
                iv.getStyleClass().add("cell-image");
                if (pc instanceof Hero) {
                    iv.setImage(heroImg);
                } else if (pc instanceof Enemy) {
                    iv.setImage(enemyImg);
                } else if (pc instanceof Treasure) {
                    iv.setImage(treasureImg);
                } else if (pc instanceof Wall) {
                    iv.setImage(wallImg);
                } else if (pc instanceof Exit) {
                    iv.setImage(exitImg);
                }
                grid.add(iv, c, r);
            }
        }

        /* --- status bar --- */
        Label scoreLabel = new Label("Score: " + model.getCurScore());
        scoreLabel.getStyleClass().add("score-label");
        Label levelLabel = new Label("Level: " + model.getLevel());
        levelLabel.getStyleClass().add("score-label");
        Label hiLabel = new Label("High: " + model.getHighScore());
        hiLabel.getStyleClass().add("score-label");
        HBox statusBar = new HBox(20, scoreLabel, levelLabel, hiLabel);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setPadding(new Insets(5));

        /* --- D-pad --- */
        Button up = new Button("↑");
        Button left = new Button("←");
        Button right = new Button("→");
        Button down = new Button("↓");
        up.getStyleClass().add("dpad-button");
        left.getStyleClass().add("dpad-button");
        right.getStyleClass().add("dpad-button");
        down.getStyleClass().add("dpad-button");
        up.setOnAction(e -> controller.moveUp());
        left.setOnAction(e -> controller.moveLeft());
        right.setOnAction(e -> controller.moveRight());
        down.setOnAction(e -> controller.moveDown());
        GridPane dpad = new GridPane();
        dpad.getStyleClass().add("dpad-grid");
        dpad.setHgap(8);
        dpad.setVgap(8);
        dpad.setAlignment(Pos.CENTER);
        dpad.add(up,    1, 0);
        dpad.add(left,  0, 1);
        dpad.add(down,  1, 1);
        dpad.add(right, 2, 1);
        VBox footer = new VBox(dpad);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));

        /* --- assemble --- */
        root.setTop(statusBar);
        root.setCenter(grid);
        root.setBottom(footer);
    }

    @Override
    public Parent render() {
        build();
        return root;
    }

    @Override
    public void update() {
        if (model.getStatus() == Model.STATUS.END_GAME) {
            TitleScreenView tv = new TitleScreenView(controller, model, stage);
            model.addObserver(tv);
            stage.getScene().setRoot(tv.render());
        } else {
            stage.getScene().setRoot(render());
        }
        root.requestFocus();  // keep arrow‐keys active
    }
}