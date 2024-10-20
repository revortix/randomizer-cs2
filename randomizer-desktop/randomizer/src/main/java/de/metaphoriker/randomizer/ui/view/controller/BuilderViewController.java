package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

@View
public class BuilderViewController {

  private final BuilderViewModel builderViewModel;

  @FXML private TextField searchField;
  @FXML private VBox actionsVBox;
  @FXML private VBox actionSequencesVBox;
  @FXML private VBox builderVBox;

  @Inject
  public BuilderViewController(BuilderViewModel builderViewModel) {
    this.builderViewModel = builderViewModel;
    Platform.runLater(this::initialize);
  }

  private void initialize() {
    setupBindings();
    fillActions();
    fillActionSequences();
    setupDrop(builderVBox);

    actionsVBox
        .getChildren()
        .forEach(
            node -> {
              if (node instanceof TitledPane) {
                VBox content = (VBox) ((TitledPane) node).getContent();
                content
                    .getChildren()
                    .forEach(
                        child -> {
                          if (child instanceof Label) {
                            setupDrag((Label) child);
                          }
                        });
              }
            });
  }

  private void setupBindings() {
    builderViewModel
        .getCurrentActionSequenceProperty()
        .addListener((_, _, newSequenceName) -> fillBuilderWithActionsOfSequence(newSequenceName));

    setupSearchFieldListener();
  }

  private void setupDrag(Label label) {
    label.setOnDragDetected(
        dragEvent -> {
          Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
          dragboard.setDragView(label.snapshot(null, null), dragEvent.getX(), dragEvent.getY());

          ClipboardContent content = new ClipboardContent();
          content.putString(label.getText());
          dragboard.setContent(content);

          dragEvent.consume();
        });
  }

  private void setupDrop(VBox target) {
    target.setOnDragOver(
        dragEvent -> {
          if (dragEvent.getGestureSource() != target && dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
          }
          dragEvent.consume();
        });

    target.setOnDragDropped(
        dragEvent -> {
          Dragboard dragboard = dragEvent.getDragboard();
          boolean success = false;

          if (dragboard.hasString()) {
            String actionText = dragboard.getString();

            Label actionLabel = new Label(actionText);
            setupDrag(actionLabel);

            target.getChildren().add(actionLabel);
            success = true;
          }

          dragEvent.setDropCompleted(success);
          dragEvent.consume();
        });
  }

  private void setupSearchFieldListener() {
    searchField
        .textProperty()
        .addListener(
            (_, _, newValue) -> {
              actionsVBox.getChildren().clear();
              String filter = newValue.toLowerCase();
              builderViewModel
                  .getActionToTypeMap()
                  .forEach(
                      (type, actionList) -> {
                        List<String> filteredActions =
                            actionList.stream()
                                .filter(action -> action.toLowerCase().contains(filter))
                                .toList();

                        if (!filteredActions.isEmpty()) {
                          actionsVBox.getChildren().add(createTitledPane(type, filteredActions));
                        }
                      });
            });
  }

  private TitledPane createTitledPane(String type, List<String> actions) {
    TitledPane titledPane = new TitledPane();
    titledPane.setCollapsible(true);
    titledPane.setAnimated(true);
    titledPane.setExpanded(false);
    titledPane.setText(type);

    VBox vBox = new VBox();
    actions.forEach(action -> vBox.getChildren().add(new Label(action)));
    applyExpandListener(titledPane);
    titledPane.setContent(vBox);

    return titledPane;
  }

  private void fillActions() {
    builderViewModel
        .getActionToTypeMap()
        .forEach(
            (type, actionList) ->
                actionsVBox.getChildren().add(createTitledPane(type, actionList)));
  }

  private void applyExpandListener(TitledPane titledPane) {
    titledPane
        .expandedProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue) {
                actionsVBox.getChildren().stream()
                    .filter(node -> node instanceof TitledPane && node != titledPane)
                    .forEach(node -> ((TitledPane) node).setExpanded(false));
              }
            });
  }

  private void fillActionSequences() {
    builderViewModel
        .getActionSequences()
        .forEach(
            actionSequence -> {
              Label label = new Label(actionSequence);
              label.setOnMouseClicked(
                  _ -> builderViewModel.getCurrentActionSequenceProperty().set(actionSequence));
              actionSequencesVBox.getChildren().add(label);
            });
  }

  private void fillBuilderWithActionsOfSequence(String sequenceName) {
    List<String> actions = builderViewModel.getActionsOfSequence(sequenceName);
    builderVBox.getChildren().clear();
    actions.forEach(action -> builderVBox.getChildren().add(new Label(action)));
  }
}
