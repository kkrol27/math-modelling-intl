package main;

import java.util.Scanner;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import maths.Mat3f;
import maths.Mat4f;
import maths.Vec3f;
import maths.Vec4f;
import waves.Wave;
import waves.WaveShader;

public class Main {
	
	/** Useful constants */
	public static final float 	PI = (float) Math.PI,
							 	RENDER_FOV = PI / 6.0f,
							 	RENDER_NEAR_PLANE = 0.1f,
							 	RENDER_FAR_PLANE = 340.0f;
	
	private static Mat3f vect_mat3(float yaw) {
		return new Mat3f().setYRotation(-yaw);
	}
	
	private static Mat4f orth_mat4() {
		return new Mat4f().setMatrix(
				new Vec4f(1.0f / 80.0f, 0.0f, 0.0f, 0.0f),
				new Vec4f(0.0f, 1.0f / 45.0f, 0.0f, 0.0f),
				new Vec4f(0.0f, 0.0f, -2.0f / 199.0f, 0.0f),
				new Vec4f(0.0f, 0.0f, -201.0f / 199.0f, 1.0f)
			);
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
		Mat4f camera = new Mat4f().setTranslation(pos.getScaledBy(-1.0f));
		camera.addYRotation(-yaw);
		camera.addXRotation(-pitch);
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
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
		
		Vec3f cam_pos = new Vec3f(0.0f, 30.0f, 0.0f);
		float cam_yaw = - PI / 2.0f;
		float cam_pit = -5.0f * PI / 180.0f;
		
		Mat4f view, proj = proj_mat4();
		Mat3f vect = new Mat3f().setIdentity();
		
		Wave wave = new Wave(2400, 240.0d, -80.0d, -80.0d);
		
		WaveShader sh = new WaveShader();
		sh.start();
		sh.load_proj_mat(proj);
		sh.load_vect_mat(vect);
		
		// End GL and Scene initialization
		// -----
		
		// -----
		// Start Render Loop
		
		boolean ortho = false;
		while(!GLFW.glfwWindowShouldClose(window)) {
			
			final float CAM_RT = 0.05f;
			final float CAM_MV = 0.05f;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == 1)
				cam_yaw -= CAM_RT;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == 1)
				cam_yaw += CAM_RT;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == 1)
				cam_pit -= CAM_RT;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == 1)
				cam_pit += CAM_RT;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_E) == 1)
				cam_pos.y += CAM_MV;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_Q) == 1)
				cam_pos.y -= CAM_MV;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_P) == 1)
				ortho = !ortho;
			if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_O) == 1) {
				float[] f = new float[2];
				get_attitude(f);
				cam_yaw = f[0];
				cam_pit = f[1];
				cam_pos = new Vec3f();
			}
			view = view_mat4(cam_pos, cam_yaw, cam_pit);
			vect = vect_mat3(cam_yaw);
			sh.load_proj_mat(ortho ? orth_mat4() : proj_mat4());
			sh.load_view_mat(view);
			sh.load_vect_mat(vect);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			GL30.glBindVertexArray(wave.vao());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			
			GL11.glDrawElements(GL11.GL_TRIANGLES, wave.count(), GL11.GL_UNSIGNED_INT, 0);
			
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
			
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
	
	/** Get attitude input from the user */
	private static void get_attitude(float[] f) {
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter the yaw: ");
		f[0] = scan.nextFloat() * PI / 180.0f;
		System.out.print("Enter the pitch: ");
		f[1] = scan.nextFloat() * PI / 180.0f;
		scan.close();
	}
}
