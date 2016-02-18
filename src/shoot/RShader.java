package shoot;

import org.lwjgl.util.vector.Matrix4f;

import shaders.Shader;

public class RShader extends Shader {

	private static final String VERTEX_FILE = "src/shoot/PVShader.txt";
	private static final String FRAGMENT_FILE = "src/shoot/PFShader.txt";

	private int location_modelViewMatrix;
	private int location_projectionMatrix;

	public RShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	protected void loadModelViewMatrix(Matrix4f modelView) {
		super.loadMatrix(location_modelViewMatrix, modelView);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
