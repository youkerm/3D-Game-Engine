package engine.input;

/**
 * Created by Mitch on 9/6/2015.
 */
public interface InputEvent {

    /* Assigns the priority of an inputEvent
     * Priority 1  is the highest
     * Priority 5 is the lowest.
     */
    int HIGHEST_PRIORITY = 1;
    int MIDDLE_PRIORITY = 2;
    int LOWEST_PRIORITY = 3;
    int NO_PRIORITY = 4;

    void onKeyPressed();
    void onKeyReleased();
    void onKeyTyped();
    void onMouseClick();
    void onMouseMove();
    void onMouseReleased();
    void onMouseWheel();
    int priority();
}
