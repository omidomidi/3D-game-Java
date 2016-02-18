package renderer;

import java.util.List;
import java.util.Map;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import Materials.Material;
import models.Raw;
import models.Textured;
import shaders.Static;
import textures.ModelTexture;
import Matrices.Calculate;

public class MaterialRenderer {
	

	

	private Static shader;
	
	
	public MaterialRenderer(Static shader, Matrix4f projectionMatrix){
		this.shader = shader;


		shader.start();
		shader.loadprojectionMatrix(projectionMatrix);
		shader.stop();
	}
	

	
	public void render(Map<Textured,List<Material>> entities){
		for(Textured model:entities.keySet()){
			prepareTexturedModel(model);
			List<Material> batch = entities.get(model);
			for(Material entity:batch){
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(Textured model){
		Raw rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		shader.loadVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	private void unbindTexturedModel(){
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Material entity){
		Matrix4f transformationMatrix = Calculate.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}


	

}
