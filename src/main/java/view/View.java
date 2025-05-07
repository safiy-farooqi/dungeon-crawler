package view;

import controller.Controller;
import model.Model;
import model.Model.STATUS;
import model.Observer;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class View implements FXComponent, Observer {
    private final Controller controller;
    private final Model model;
    private final Stage stage;
    private final TitleScreenView titleView;
    private final GameView gameView;

    public View(Controller controller, Model model, Stage stage) {
        this.controller = controller;
        this.model = model;
        this.stage = stage;
        this.titleView = new TitleScreenView(controller, model, stage);
        this.gameView = new GameView(controller, model, stage);

        model.addObserver(this);
    }

    @Override
    public Parent render() {
        if (model.getStatus() == STATUS.END_GAME) {
            return titleView.render();
        } else {
            return gameView.render();
        }
    }

    @Override
    public void update() {
        stage.getScene().setRoot(render());
    }
}
