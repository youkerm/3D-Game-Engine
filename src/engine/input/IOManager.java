package engine.input;

import engine.core.tools.Time;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOManager {

	private static Map<Integer, List<InputEvent>> inputs = new HashMap<Integer, List<InputEvent>>();

    private static long lastUpdateTime;

	public IOManager(InputEvent... inputEvent) {
		for (InputEvent input: inputEvent) {
			add(input);
		}
	}

	public static void add(InputEvent inputEvent) {
		int priority = inputEvent.priority();
		List<InputEvent> list = inputs.get(priority);
		if (list == null) {
			list = new ArrayList<InputEvent>();
			inputs.put(priority, list);
		}
		list.add(inputEvent);
	}

	private void remove(InputEvent inputEvent) {
		int priority = inputEvent.priority();
		List<InputEvent> list = inputs.get(priority);
		if (list != null) {
			if (list.size() > 1) {
				list.remove(inputEvent);
			} else {
				inputs.remove(priority);
			}
		}
	}

	public void update() {
		while (Mouse.next()) {
			for (int priority = InputEvent.HIGHEST_PRIORITY; priority < InputEvent.NO_PRIORITY; priority++) {
				List<InputEvent> list =  inputs.get(priority);
				if (list != null) {
					for (InputEvent inputEvent : list) {
						updateMouseEvents(inputEvent);
					}
				}
			}
		}
		while (Keyboard.next()) {
			for (int priority = InputEvent.HIGHEST_PRIORITY; priority < InputEvent.NO_PRIORITY; priority++) {
				List<InputEvent> list =  inputs.get(priority);
				if (list != null) {
					for (InputEvent inputEvent : list) {
						updateKeyboardEvents(inputEvent);
					}
				}
			}
		}
        if (Keyboard.getEventKeyState()) {
            //unpolled input to keyboard
            if (getLastUpdateTime() >= 100) {
                for (int priority = InputEvent.HIGHEST_PRIORITY; priority < InputEvent.NO_PRIORITY; priority++) {
                    List<InputEvent> list = inputs.get(priority);
                    if (list != null) {
                        for (InputEvent inputEvent : list) {
                            inputEvent.onKeyTyped();
                        }
                    }
                    lastUpdateTime = Time.getCurrentTime();
                }
            }
        }
	}

	private void updateMouseEvents(InputEvent inputEvent) {
		if (Mouse.getEventButton() >= 0) {
			if (Mouse.getEventButtonState()) {
				inputEvent.onMouseClick();
			} else {
				inputEvent.onMouseReleased();
			}
		} else {
			inputEvent.onMouseMove();
		}
		if (Mouse.getDWheel() != 0) {
			inputEvent.onMouseWheel();
		}
	}

	private void updateKeyboardEvents(InputEvent inputEvent) {
		if (Keyboard.getEventKeyState()) {
			inputEvent.onKeyPressed();
		} else {
			inputEvent.onKeyReleased();
		}
	}

	public static char getCharacterPressed() {
		char key = Keyboard.getEventCharacter();
		return key;
	}

    public static long getLastUpdateTime() {
        return Time.getCurrentTime() - lastUpdateTime;
    }

}
