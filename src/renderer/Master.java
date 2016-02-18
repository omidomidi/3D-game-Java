package renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import Materials.Camera;
import Materials.Material;
import Materials.Light;
import models.Textured;
import shaders.Static;
import skybox.SkyboxRenderer;

public class Master {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 2000;
	
	private static final float RED = 0;
	private static final float GREEN = 0;
	private static final float BLUE = 0;
	
	private Matrix4f projectionMatrix;
	
	private Static shader = new Static();
	private MaterialRenderer renderer;
	
	private Map<Textured, List<Material>> entities = new HashMap<Textured, List<Material>>();
	
	private SkyboxRenderer skyboxRenderer;
	
	public Master(Loader loader){
		//GL11.glEnable(GL11.GL_CULL_FACE);
		//++GL11.glCullFace(GL11.GL_BACK);
		createProjectionMatrix();
		renderer = new MaterialRenderer(shader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
	}
	
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
	
	public void render(Light sun, Camera camera){
		prepare();
		shader.start();
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		skyboxRenderer.render(camera);
		entities.clear();
	}
	
	public void processEntity(Material entity){
		Textured entityModel = entity.getModel();
		List<Material> batch = entities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Material>newBatch = new ArrayList<Material>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}
	
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);	
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}
	
	private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
	}

}
