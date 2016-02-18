package Materials;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private float distanceFromPlayer = 30;
	private float angleAroundPlayer = 180;

	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 10;
	private float yaw = 0;
	private float roll;

	private Player player;

	public Camera(Player player) {
		this.player = player;
	}

	public void move() {
		position();
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
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

	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
        if(distanceFromPlayer<5){
            distanceFromPlayer = 5;
        }
	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}

	private void calculateAngleAroundPlayer() {
		if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
	
	private void position(){
		if(Keyboard.isKeyDown(Keyboard.KEY_I)){
			distanceFromPlayer = 0f;
			angleAroundPlayer = 180;
			pitch = 0;
			yaw = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_O)){
			distanceFromPlayer = 30;
			angleAroundPlayer = 180;
			pitch = 10;
			yaw = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Z)){
			distanceFromPlayer -=1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_X)){
			distanceFromPlayer +=1;

		}
	}
	
	public void resetPosition() {
		distanceFromPlayer = 30;
		angleAroundPlayer = 180;
		pitch = 20;
		yaw = 0;
	}
	

}