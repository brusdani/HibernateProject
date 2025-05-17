package com.example.databaseapplication.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;

import java.util.Collection;
import java.util.Objects;

public class FXUtils {
    public static void bindUiToTask(
            Task<?> task,
            Node overlay,
            ProgressIndicator spinner,
            Collection<Node> alwaysDisable,
            Collection<Button> disableUnlessSelected,
            ReadOnlyObjectProperty<?> selectedItem
    ) {
        overlay.visibleProperty().bind(task.runningProperty());
        spinner.visibleProperty().bind(task.runningProperty());

        // always off while running
        alwaysDisable.forEach(n -> n.disableProperty().bind(task.runningProperty()));

        // only enabled when BOTH not running AND something is selected
        BooleanBinding noSel = Bindings.isNull(selectedItem);
        BooleanBinding busyOrNoSel = task.runningProperty().or(noSel);
        disableUnlessSelected.forEach(b -> b.disableProperty().bind(busyOrNoSel));
    }
    public static void bindUiToTask(Task<?> task,
                                    Node overlay,
                                    ProgressIndicator spinner,
                                    Node... controlsToDisable) {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(overlay, "overlay");
        Objects.requireNonNull(spinner, "spinner");

        // show/hide overlay & spinner
        overlay.visibleProperty().bind(task.runningProperty());
        spinner.visibleProperty().bind(task.runningProperty());

        // disable all the passed controls while running
        for (Node ctrl : controlsToDisable) {
            ctrl.disableProperty().bind(task.runningProperty());
        }
    }
}

