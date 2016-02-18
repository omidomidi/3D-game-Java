package models;

import textures.ModelTexture;

public class Textured {
	
	private Raw rawModel;
	private ModelTexture texture;
	
	public Textured (Raw model, ModelTexture texture){
		this.rawModel = model;
		this.texture = texture;
	}

	public Raw getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
	

}
