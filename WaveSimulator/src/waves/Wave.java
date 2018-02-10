package waves;

import java.util.Random;

import main.GLL;

/** Class responsible for constructing a wave mesh from ocean spectra */
public class Wave {

	/** Wave vao id */
	private int vao;
	/** Wave vertex count */
	private int count;
	
	/**
	 * Generates a wave and a corresponding mesh.
	 * 
	 * @param wm
	 * 		
	 * @param hm
	 * @param vc
	 * @param d
	 * @param xo
	 * @param zo
	 */
	public Wave(double wm, double hm, int vc, double d, double xo, double zo) {
		// Wave components
		double[][] wave_k = new double[15][2];
		double[] wave_a = new double[15];
		double[] wave_d = new double[15];
		
		// Calculate wave components
		int n = 0;
		Random rand = new Random();
		for(int w = 0; w < 3; w++) {
			for(int t = 0; t < 5; t++) {
				double ti = ((double)(t - 1)) * 0.5d;
				double wi = ((double) w) * 0.2d + wm;
				double ks = 9.81d / (2.0d * Math.PI * wi);
				wave_d[n] = 2.0d * Math.PI * rand.nextDouble();
				wave_a[n] = spectrum(wi, ti, hm);
				wave_k[n][0] = ks * Math.cos(wi);
				wave_k[n][1] = -ks * Math.sin(wi);
				n++;
			}
		}
		
		// Generate mesh
		float[] mesh_p = new float[3 * vc * vc];
		float[] mesh_n = new float[3 * vc * vc];
		for(int i = 0; i < vc; i++) {
			for(int k = 0; k < vc; k++) {
				double x = xo +(((double) i) / ((double)(vc - 1))) * d;
				double z = zo +(((double) k) / ((double)(vc - 1))) * d;
				n = 3 * (i + vc * k);
				// Mesh coordinates
				mesh_p[n + 0] = (float) x;
				mesh_p[n + 1] = (float) displacement(wave_a, wave_k, wave_d, xo, zo, x, z);
				mesh_p[n + 2] = (float) z;
				// Mesh normal vector
				double dx1 = 0.001d, dy1 = displacement(wave_a, wave_k, wave_d, xo, zo, x + 0.001d, z) - mesh_p[n + 1], dz1 = 0.0d;
				double dx2 = 0.0d, dy2 = displacement(wave_a, wave_k, wave_d, xo, zo, x, z + 0.001d) - mesh_p[n + 1], dz2 = 0.001d;
				double xn = dy2 * dz1 - dy1 * dz2, yn = dx1 * dz2 - dx2 * dz1, zn = dx2 * dy1 - dx1 * dy2;
				double nm = Math.sqrt(xn*xn + yn*yn + zn*zn);
				mesh_n[n + 0] = (float)(xn / nm);
				mesh_n[n + 1] = (float)(yn / nm);
				mesh_n[n + 2] = (float)(zn / nm);
			}
		}
		
		// Generate index array
		n = 0;
		int[] mesh_i = new int[6 * (vc - 1) * (vc - 1)];
		for(int i = 0; i < vc - 1; i++) {
			for(int k = 0; k < vc - 1; k++) {
				int a = i + vc * k;
				mesh_i[n++] = a;
				mesh_i[n++] = a + vc + 1;
				mesh_i[n++] = a + 1;
				mesh_i[n++] = a;
				mesh_i[n++] = a + vc;
				mesh_i[n++] = a + vc + 1;
			}
		}
		
		// Load data to vao and set vao id
		this.vao = GLL.load_vao(mesh_p, mesh_n, mesh_i);
		this.count = mesh_i.length;
	}
	
	/** Returns the wave vao */
	public int vao() {
		return vao;
	}
	
	/** Returns the wave vertex count */
	public int count() {
		return count;
	}
	
	/** Returns specturm amplitude */
	private static double spectrum(double w, double a, double h)  {
		double c = 8.1d * 9.81d * 9.81d / (1000.0d * Math.pow(w, 5));
		double e = Math.exp(-0.032d * 9.81d * 9.81d / (h * h * Math.pow(w, 4.0d)));
		double d = 2.239199d * Math.pow(Math.cos(a / 2.0d), 31);
		return Math.sqrt(2.0d * c * e * d);
	}
	
	/** Returns the water displacement */
	private static double displacement(double[] a, double[][] k, double[] d, double xo, double zo, double x, double z) {
		double v = 0.0d;
		for(int i = 0; i < a.length; i++)
			v += a[i] * Math.cos(k[i][0] * (x - xo ) + k[i][1] * (z - zo) - d[i]);
		return v;
	}
}
