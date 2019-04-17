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
