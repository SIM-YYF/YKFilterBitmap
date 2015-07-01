package com.test.api;


public class MixedMode {


	
	/**
	 * 混合模式为：绿色 
	 * 公式：A = 1-(1-A)*(1-B)
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	public static void Screen(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = (short) (255 - (((255 - src_r.value) * (255 - r)) >> 8));
		src_g.value = (short) (255 - (((255 - src_g.value) * (255 - g)) >> 8));
		src_b.value = (short) (255 - (((255 - src_b.value) * (255 - b)) >> 8));
	}

	/**
	 * 混合模式：绿色 + alpha
	 * 公式：A = (1-((1-A)*(1-B) / 2^8)) * a / 1 + A * (1 - a) / 1
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	public static void Screen(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) (255 - (((255 - src_r.value) * (255 - r)) >> 8));
		gVal = (short) (255 - (((255 - src_g.value) * (255 - g)) >> 8));
		bVal = (short) (255 - (((255 - src_b.value) * (255 - b)) >> 8));
		
		src_r.value = (short) (rVal * alpha / 255 + src_r.value * (255 - alpha) / 255);
		src_g.value = (short) (gVal * alpha / 255 + src_g.value * (255 - alpha) / 255);
		src_b.value = (short) (bVal * alpha / 255 + src_b.value * (255 - alpha) / 255);
	}


	/**
	 * 混合模式：柔光
	 * B <= 127
	 * A = (((2 * B) - 1) * (1 * A - A^2)/1^2 + A)
	 * B > 127
	 * A = ((2 * B) - 1) * (sqrt(1) * sqrt(A) - A) / 1 + A
	 * @param src_r 原
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	public static void SoftLight(
			Myshort src_r, 
			Myshort src_g, 
			Myshort src_b,
			short r, 
			short g, 
			short b) {
		
		if (r <= 127) {
			src_r.value = (short) ((2 * r - 255) * (255 * src_r.value - src_r.value^2) / 255^2 + src_r.value);
		} else {
			src_r.value =  (short) ((2 * r - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_r.value) - src_r.value) / 255 + src_r.value);
		}
		
		if (g <= 127) {
			src_g.value = (short) ((2 * g - 255) * (255 * src_g.value - src_g.value^2) / 255^2 + src_g.value);
		} else {
			src_g.value =  (short) ((2 * g - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_g.value) - src_g.value) / 255 + src_g.value);
		}
		
		if (b <= 127) {
			src_b.value = (short) ((2 * b - 255) * (255 * src_b.value - src_b.value^2) / 255^2 + src_b.value);
		} else {
			src_b.value =  (short) ((2 * b - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_b.value) - src_b.value) / 255 + src_b.value);
		}

	}

	/**
	 * 混合模式：柔光  + alpha
	 * B <= 127
	 * A = (((2 * B) - 1) * (1 * A - A^2)/1^2 + A) * a / 255 + A * (1 - a) / 1
	 * 
	 * B > 127
	 * A = (((2 * B) - 1) * (sqrt(1) * sqrt(A) - A) / 1 + A) * a / 255 + A * (1 - a) / 1
	 * 
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	public static void SoftLight(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b, short alpha) {
		short rVal, gVal, bVal;

		if (r <= 127) {
			rVal = (short) ((2 * r - 255) * (255 * src_r.value - src_r.value^2) / 255^2 + src_r.value);
		} else {
			rVal =  (short) ((2 * r - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_r.value) - src_r.value) / 255 + src_r.value);
		}
		
		if (g <= 127) {
			gVal = (short) ((2 * g - 255) * (255 * src_g.value - src_g.value^2) / 255^2 + src_g.value);
		} else {
			gVal =  (short) ((2 * g - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_g.value) - src_g.value) / 255 + src_g.value);
		}
		
		if (b <= 127) {
			bVal = (short) ((2 * b - 255) * (255 * src_b.value - src_b.value^2) / 255^2 + src_b.value);
		} else {
			bVal =  (short) ((2 * b - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_b.value) - src_b.value) / 255 + src_b.value);
		}
		
		src_r.value = (short) (rVal * alpha / 255 + src_r.value * (255 - alpha) / 255);
		src_g.value = (short) (gVal * alpha / 255 + src_g.value * (255 - alpha) / 255);
		src_b.value = (short) (bVal * alpha / 255 + src_b.value * (255 - alpha) / 255);
	}
	
	/**
	 * 混合模式：排除
	 * 公式： A = (B > A) ? (B - A) : (A - B)
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	public static void Difference(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = (short) ((r > src_r.value) ? (r - src_r.value): (src_r.value - r));
		src_g.value = (short) ((g > src_g.value) ? (g - src_g.value): (src_g.value - g));
		src_b.value = (short) ((b > src_b.value) ? (b - src_b.value): (src_b.value - b));
	}
	
	
}
