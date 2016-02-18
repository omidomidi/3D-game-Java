package shoot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import Materials.Camera;
import renderer.Loader;

public class ShootMaster {
	public static List<Shoot> particles = new ArrayList<Shoot>();
	private static ShootRenderer renderer;
	
	public static void init(Loader loader, Matrix4f projectionMatrix){
		renderer = new ShootRenderer(loader, projectionMatrix);
	}
	
	public static void update(){
		Iterator<Shoot> iterator = particles.iterator();
		while (iterator.hasNext()){
			Shoot p = iterator.next();
			boolean stillAlive = p.update();
			if(!stillAlive){
				iterator.remove();
			}
		}
	}
	
	public static void renderParticles(Camera camera){
		renderer.render(particles, camera);
	}
	
	public static void cleanUp(){
		renderer.cleanUp();
	}
	
	public static void addParticle(Shoot particle){
		particles.add(particle);
	}

}
