package HUDs;
import org.lwjgl.util.vector.Matrix4f;

import shaders.Shader;

public class HUDShader extends Shader {

	private static final String VERTEX_FILE = "src/HUDs/hudVShader.txt";
	private static final String FRAGMENT_FILE = "src/HUDs/hudFShader.txt";

	private int location_transformationMatrix;

	public HUDShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void loadTransformation(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
