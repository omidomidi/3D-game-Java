package Materials;

import java.util.Vector;

import org.lwjgl.input.Keyboard;

import org.lwjgl.util.vector.Vector3f;

import models.Textured;

import renderer.DisplayManager;

public class Player extends Material{
	
	private static final float RUN_SPEED = 30;
	private static final float TURN_SPEED = 30;
	
	public float currentSpeed = 0;
	public float currentTurnSpeed = 0;
	public float dy = 0;

	float tleft = 0;
	float tright = 0;
	
	public float currentSpeed2 = 0;
	public float currentTurnSpeed2 = 0;
	public float dy2 = 0;
	
	
	public static Vector<Float> xpos = new Vector<Float>();
	public static Vector<Float> ypos = new Vector<Float>();
	public static Vector<Float> zpos = new Vector<Float>();
	public static Vector<Float> turn = new Vector<Float>();
	

	public Player(Textured model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
	}
	
	public void move(){
		checkInputs();
		
		float dr = (float) currentTurnSpeed * DisplayManager.getFrameTimeSeconds();

		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();

		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, dy* DisplayManager.getFrameTimeSeconds(), dz);

		xpos.add(dx);
		ypos.add(dy * DisplayManager.getFrameTimeSeconds());
		zpos.add(dz);
		turn.add(dr);
		
		


		
		
	}
	
	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			this.currentSpeed = -RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			this.currentSpeed = RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			dy = RUN_SPEED;
			
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			dy = -RUN_SPEED;
		} else {
			dy = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			this.currentTurnSpeed = -TURN_SPEED;
			
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			this.currentTurnSpeed = TURN_SPEED;

		} else {
			this.currentTurnSpeed = 0;
		}
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_P)){
			Thread one = new Thread(){
				public void run(){
					playback();
				}
			};
			one.start();
			
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			/*this runs the playback in reverse
			I made a new thread so that the drawing would happen while it was running */
			Thread one = new Thread(){
				public void run(){
					reversePlayback();
				}
			};
			one.start();
			
		}

	}



	public void playback(){
		
		super.increaseRotation(0, -this.getRotY(), 0);
		this.setPosition(new Vector3f(0,-3f,-15));
		for(int i=0; i<xpos.size(); i++){
				super.increaseRotation(0, turn.get(i)/3, 0);
				super.increasePosition(xpos.get(i)/3, ypos.get(i)/3, zpos.get(i)/3);
				
				//pauses the thread after every point to let the drawing update;
			try {
			    Thread.sleep(20);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}	
		
		//after playback is done, resets each of the vectors
		xpos = new Vector<Float>();
		ypos = new Vector<Float>();
		zpos = new Vector<Float>();
		turn = new Vector<Float>();
	}
	
	public void reversePlayback(){
		for(int i=xpos.size()-1; i>=0; i--){
				super.increaseRotation(0, -turn.get(i), 0);
				super.increasePosition(-xpos.get(i), -ypos.get(i), -zpos.get(i));
			try {
			    Thread.sleep(40);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		xpos = new Vector<Float>();
		ypos = new Vector<Float>();
		zpos = new Vector<Float>();
		turn = new Vector<Float>();
		
	}
	
	public void move2(){
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
	
		super.increasePosition(dx, dy* DisplayManager.getFrameTimeSeconds(), dz);
	}
}
