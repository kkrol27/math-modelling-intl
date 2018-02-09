package main;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import maths.Mat4f;
import maths.Vec3f;
import maths.Vec4f;
import waves.WaveShader;

public class Main {
	
	/** Useful constants */
	public static final float 	PI = (float) Math.PI,
							 	RENDER_FOV = PI / 6.0f,
							 	RENDER_NEAR_PLANE = 0.1f,
							 	RENDER_FAR_PLANE = 340.0f;
	
	private static Mat4f ortho_mat4() {
		// TODO
		return null;
	}
	
	private static Mat4f proj_mat4() {
		final float scale = (float) ((1.0f / Math.tan(RENDER_FOV)));
		final float frustum_length = RENDER_NEAR_PLANE - RENDER_FAR_PLANE;
		return new Mat4f().setMatrix(
				new Vec4f(scale / (1280.0f / 720.0f), 0.0f, 0.0f, 0.0f),
				new Vec4f(0.0f, scale, 0.0f, 0.0f),
				new Vec4f(0.0f, 0.0f, (RENDER_FAR_PLANE + RENDER_NEAR_PLANE) / frustum_length, -1.0f),
				new Vec4f(0.0f, 0.0f, 2.0f * RENDER_NEAR_PLANE * RENDER_FAR_PLANE / frustum_length, 0.0f)
			);
	}
	
	/** Generates the camera matrix for the specified camera parameters */
	private static Mat4f view_mat4(Vec3f pos, float yaw, float pitch) {
		Mat4f camera = new Mat4f().setTranslation(pos);
		camera.addYRotation(-pitch);
		camera.addYRotation(-yaw);
		return camera;
	}

	/** Program starts here */
	public static void main(String[] args) {
		
		// ----------
		// Start GLFW initialization
		
		if(!GLFW.glfwInit())
			throw new IllegalStateException();
		
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
		GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, 60);
		GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, 24);
		
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		
		final long window = GLFW.glfwCreateWindow(1280, 720, "Wave Simulator", 0, 0);
		if(window == 0)
			throw new IllegalStateException();
		
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSwapInterval(1);
		GLL.init();
		GLFW.glfwShowWindow(window);
		GLFW.glfwPollEvents();
		
		// End GLFW initialization
		// -----
		
		// ----------
		// Start GL and Scene initialization
		
		GL11.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
		
		Vec3f cam_pos = new Vec3f(0.0f, 10.0f, 0.0f);
		float cam_yaw = PI / 2.0f;
		float cam_pit = 0.0f;
		
		Mat4f view, proj = proj_mat4();
		
		WaveShader sh = new WaveShader();
		sh.start();
		sh.load_proj_mat(proj);
		
		
		// End GL and Scene initialization
		// -----
		
		// -----
		// Start Render Loop
		
		while(!GLFW.glfwWindowShouldClose(window)) {
			
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == 1)
				cam_yaw -= 0.01f;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == 1)
				cam_yaw += 0.01f;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == 1)
				cam_pit -= 0.01f;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == 1)
				cam_pit += 0.01f;
			view = view_mat4(cam_pos, cam_yaw, cam_pit);
			sh.load_view_mat(view);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			GLFW.glfwSwapBuffers(window);
			GLFW.glfwPollEvents();
		}
		
		// End Render Loop
		// -----
		
		// -----
		// Start Termination
		
		sh.terminate();
		GLL.terminate();
		GLFW.glfwTerminate();
		
		// End Termination
		// -----
		
	}
}
