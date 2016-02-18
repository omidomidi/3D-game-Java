package shoot;

import org.lwjgl.util.vector.Vector3f;

import renderer.DisplayManager;

public class Shoot {

	private Vector3f position;
	private Vector3f velocity;
	private float lifeLength;
	
	private float elapsedTime = 0;

	public Shoot(Vector3f position, Vector3f velocity, float lifeLength) {
		
		this.position = position;
		position.y -=1;
		this.velocity = velocity;
		this.lifeLength = lifeLength;
		ShootMaster.addParticle(this);
	}

	public Vector3f getPosition() {
		return position;
	}
	
	protected boolean update(){
		velocity.z += DisplayManager.getFrameTimeSeconds();
		
		Vector3f change = new Vector3f(velocity);
		change.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(change, position, position);
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		return elapsedTime < lifeLength;
	}
}
