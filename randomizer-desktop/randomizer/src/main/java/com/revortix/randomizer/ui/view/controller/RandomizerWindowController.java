package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.util.GifDecoder;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.ViewProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@View
public class RandomizerWindowController implements Initializable {

    private static final String GIF_RESOURCE_PATH = "com/revortix/randomizer/gif/its-time.gif";

    private final ViewProvider viewProvider;

    @FXML
    private VBox root;
    @FXML
    private StackPane stackPane;
    @FXML
    private GridPane contentPane;
    @FXML
    private VBox navigationBarHolder;

    @Inject
    public RandomizerWindowController(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadNavigationBar();

        registerViewListener();
        setupControlBarClickTransparency();

        addPreloadingGif();
        loadHomeView();
    }

    private void addPreloadingGif() {
        try {
            GifDecoder gifDecoder = new GifDecoder(GIF_RESOURCE_PATH);
            ImageView e = new ImageView(new Image(GIF_RESOURCE_PATH));

            e.fitHeightProperty().bind(root.heightProperty());
            e.fitWidthProperty().bind(root.widthProperty());

            stackPane.getChildren().add(e);
            executeAfterDelay(gifDecoder.getTotalDuration(), () -> stackPane.getChildren().remove(e));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeAfterDelay(int millis, Runnable action) {
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(millis), _ -> action.run()));
        delay.setCycleCount(1);
        delay.play();
    }

    // note: for this pickOnBounds have to be false
    private void setupControlBarClickTransparency() {
        navigationBarHolder.addEventFilter(
                MouseEvent.MOUSE_CLICKED,
                event -> {
                    if (event.getTarget() == navigationBarHolder) {
                        event.consume();
                    }
                });
    }

    private void registerViewListener() {
        viewProvider.registerViewChangeListener(HomeViewController.class, _ -> loadHomeView());
        viewProvider.registerViewChangeListener(BuilderViewController.class, _ -> loadBuilderView());
        viewProvider.registerViewChangeListener(RandomizerViewController.class, _ -> loadRandomizerView());
    }

    private void loadNavigationBar() {
        Parent controlBarParent = viewProvider.requestView(NavigationBarController.class).parent();
        navigationBarHolder.getChildren().add(controlBarParent);
    }

    private void loadHomeView() {
        Parent homeViewParent = viewProvider.requestView(HomeViewController.class).parent();
        setContent(homeViewParent);
    }

    private void loadRandomizerView() {
        Parent randomizerViewParent = viewProvider.requestView(RandomizerViewController.class).parent();
        setContent(randomizerViewParent);
    }

    private void loadBuilderView() {
        Parent builderViewParent = viewProvider.requestView(BuilderViewController.class).parent();
        setContent(builderViewParent);
    }

    private void setContent(Node node) {
        clearContent();
        if (node != null) {
            contentPane.getChildren().add(node);
        }
    }

    private void clearContent() {
        contentPane.getChildren().clear();
    }
}
