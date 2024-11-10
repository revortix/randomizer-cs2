package de.metaphoriker.randomizer.ui.view.viewmodel;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.value.Interval;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

@Getter
public class ActionSettingsViewModel {

    private final ObjectProperty<Action> actionInFocusProperty = new SimpleObjectProperty<>();
    private final IntegerProperty minIntervalProperty = new SimpleIntegerProperty();
    private final IntegerProperty maxIntervalProperty = new SimpleIntegerProperty();

    public ActionSettingsViewModel() {
        setupActionInFocusListener();
    }

    public void applyInterval() {
        Action currentAction = actionInFocusProperty.get();
        if (currentAction != null) {
            Interval interval = currentAction.getInterval();
            interval.setMin(minIntervalProperty.get());
            interval.setMax(maxIntervalProperty.get());
        }
    }

    private void reset() {
        minIntervalProperty.set(0);
        maxIntervalProperty.set(0);
    }

    private void setupActionInFocusListener() {
        actionInFocusProperty.addListener(
                (_, _, newAction) -> {
                    reset();
                    if (newAction != null) {
                        minIntervalProperty.set(newAction.getInterval().getMin());
                        maxIntervalProperty.set(newAction.getInterval().getMax());
                    }
                });
    }
}
