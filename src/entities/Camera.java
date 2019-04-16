package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private final static float MOVE_DISTANCE = 0.2f;
	
	private Vector3f position = new Vector3f(200,6,200);
	private float pitch;//how high or low the camera is aimed
	private float yaw;//how much left or right camera is aiming
	private float roll;//how much camera is tilted to one side
	
	public Camera() {
	}
	
	public void move() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.z -= MOVE_DISTANCE;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.z += MOVE_DISTANCE;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += MOVE_DISTANCE;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x -= MOVE_DISTANCE;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			position.y -= MOVE_DISTANCE;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
			position.y += MOVE_DISTANCE;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	
}
