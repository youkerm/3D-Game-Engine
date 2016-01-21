package engine.core.entity;

import engine.DisplayManager;
import engine.core.Terrain;
import engine.core.entity.component.Component;
import engine.core.entity.component.ModelSystem;
import engine.core.entity.component.PositionSystem;
import engine.core.tools.Settings;
import engine.input.IOManager;
import engine.input.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Player extends Entity implements InputEvent {

	private float WALK_SPEED = 30;
	public static final float GRAVITY = -50;
	private final float JUMP_POWER = 50;
	
	private float currentSpeed = 0, currentStrafSpeed = 0, upwardsSpeed = 0;
	
	private boolean isInAir = false;
	private boolean CAN_MOVE = true, MOVE_FORWARD = false, MOVE_BACKWARD = false, MOVE_RIGHT = false, MOVE_LEFT = false, JUMPING = false;
	
	public Player(ModelSystem model, PositionSystem position, Component... components) {
		super.addComponent(model);
		super.addComponent(position);
		for(Component component: components) {
			super.addComponent(component);
		}
		Settings.setFPSEnable(true);
		IOManager.add(this);
	}

	public void move(Terrain terrain) {
		checkMovement();
		float distance = currentSpeed * DisplayManager.getDelta();
		float dx =+ (float) (distance * Math.sin(Math.toRadians(super.getPositionSystem().getRotation().y)));
		float dz =+ (float) (distance * Math.cos(Math.toRadians(super.getPositionSystem().getRotation().y)));
		super.getPositionSystem().increasePosition(dx, 0, dz);

		float straftDistance = currentStrafSpeed * DisplayManager.getDelta();
		float dxStraft =+ (float) (straftDistance * Math.sin(Math.toRadians(super.getPositionSystem().getRotation().y + 90f)));
		float dzStraft =+ (float) (straftDistance * Math.cos(Math.toRadians(super.getPositionSystem().getRotation().y + 90f)));
		super.getPositionSystem().increasePosition(dxStraft, 0, dzStraft);
		
		upwardsSpeed += GRAVITY * DisplayManager.getDelta();
		super.getPositionSystem().increasePosition(0, upwardsSpeed * DisplayManager.getDelta(), 0);


		float terrainHeight = terrain.getHeightOfTerrain(super.getPositionSystem().getPosition().x, super.getPositionSystem().getPosition().z);
		if(super.getPositionSystem().getPosition().y<terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPositionSystem().getPosition().y = terrainHeight;
		}
	}

	public void setCanMove(boolean canMove) {
		this.CAN_MOVE = canMove;
	}
	
	private void jump() {
		if (!isInAir) {
			upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}

	private void checkMovement() {
		if (CAN_MOVE) { //Prevent from moving player while trying to get screen back to center if player moved screen
			if(MOVE_FORWARD){
				currentSpeed = WALK_SPEED;
			} else if(MOVE_BACKWARD) {
				currentSpeed = -WALK_SPEED;
			} else {
				currentSpeed = 0;
			}
			if (MOVE_RIGHT) {
				currentStrafSpeed= -WALK_SPEED/1.5f;
			} else if (MOVE_LEFT) {
				currentStrafSpeed = WALK_SPEED/1.5f;
			} else {
				currentStrafSpeed = 0;
			}
		}
		if (JUMPING) {
			jump();
		}
	}

	@Override
	public void onKeyPressed() {
        if (Settings.isFPSMode()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_W || Keyboard.getEventKey() == Keyboard.KEY_UP) {
				MOVE_FORWARD = true;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_S || Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
				MOVE_BACKWARD = true;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_A || Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
				MOVE_LEFT = true;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_D || Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
				MOVE_RIGHT = true;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) {
				WALK_SPEED = WALK_SPEED * 2;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
				jump();
			}
        }
    }

	@Override
	public void onKeyReleased() {
        if (Settings.isFPSMode()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_TAB) {
                Settings.setFPSEnable(false);
            } else if (Keyboard.getEventKey() == Keyboard.KEY_W || Keyboard.getEventKey() == Keyboard.KEY_UP) {
				MOVE_FORWARD = false;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_S || Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
				MOVE_BACKWARD = false;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_A || Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
				MOVE_LEFT = false;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_D || Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
				MOVE_RIGHT = false;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) {
				WALK_SPEED = 30;
			}
        } else {
            if (Keyboard.getEventKey() == Keyboard.KEY_TAB) {
                Settings.setFPSEnable(true);
            }
        }
	}

	@Override
	public void onKeyTyped() {

	}

	@Override
	public void onMouseClick() {

	}

	@Override
	public void onMouseMove() {
		if (!Mouse.isButtonDown(0) && Mouse.isButtonDown(1)) {

		}
	}

	@Override
	public void onMouseReleased() {

	}

	@Override
	public void onMouseWheel() {

	}

	@Override
	public int priority() {
		return InputEvent.HIGHEST_PRIORITY;
	}
}
