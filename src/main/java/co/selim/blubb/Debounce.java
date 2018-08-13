package co.selim.blubb;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

class Debounce {
    private final PauseTransition transition;

    Debounce(Runnable task) {
        this.transition = new PauseTransition(Duration.millis(100));
        this.transition.setOnFinished(e -> task.run());
    }

    void debounce() {
        transition.playFromStart();
    }
}
