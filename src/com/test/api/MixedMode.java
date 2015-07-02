package com.test.api;


public class MixedMode {


	/**
	 * 混合模式：柔光
	 * 计算公式：
	 * B <= 127
	 * A = (((2 * B) - 1) * (1 * A - A^2)/1^2 + A)
	 * B > 127
	 * A = ((2 * B) - 1) * (sqrt(1) * sqrt(A) - A) / 1 + A
	 * @param src
	 * @param req
	 * @return
	 */
	private static short calc_width_soft_light(short src, short req) {
		if (req <= 127) {
			return (short) ((2 * req - 255) * (255 * src - src^2) / 255^2 + src);
		} else {
			return (short) ((2 * req - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src) - src) / 255 + src);
		}
	}
	
	/**
	 * 混合模式：绿光
	 * 计算公式：
	 * A = (1-(((1-A)*(1-B)) << 8))
	 * @param src
	 * @param req
	 * @return
	 */
	private static short calc_width_screen(short src, short req){
		return (short) (255 - (((255- src) * (255 - req)) >> 8));
	}

	/**
	 * 混合模式：透明
	 * 计算公式：
	 * A = T * a / 1 + A * (255 - a) / 1
	 * @param T
	 * @param src
	 * @param alpha
	 * @return
	 */
	private static short calc_with_alpha(short T, short src, short alpha) {
		return (short) (T * alpha / 255 + src * (255 - alpha) / 255);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 绿色 
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	public static void screen(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = calc_width_screen(src_r.value, r);
		src_g.value = calc_width_screen(src_g.value, g);
		src_b.value = calc_width_screen(src_b.value, b);
	}

	
	
	/**
	 * 绿色 + alpha
	 * 
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	public static void sreen_alpha(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = calc_width_screen(src_r.value, r);
		gVal = calc_width_screen(src_g.value, g);
		bVal = calc_width_screen(src_b.value, b);
		
		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
		
	}

	public static void soft_light_alpha_2(Myshort src_r, Myshort src_g, Myshort src_b,short r, short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = calc_width_soft_light(src_r.value, r);
		gVal = calc_width_soft_light(src_g.value, g);
		bVal = calc_width_soft_light(src_b.value, b);

		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
	}
	
	

	/**
	 * 柔光
	 * @param src_r 原
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	public static void soft_light(
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
	 * 柔光  + alpha
	 * 
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	public static void soft_light_alpha(short r, short g, short b,short rr, short rg, short rb, short alpha) {
		short rVal, gVal, bVal;

		if (r <= 127) {
			rVal = (short) ((2 * rr - 255) * (255 * r - r^2) / 255^2 + r);
		} else {
			rVal =  (short) ((2 * rr - 255) * (Math.sqrt(255.0) * Math.sqrt((double)r) - r) / 255 + r);
		}
		
		if (g <= 127) {
			gVal = (short) ((2 * rg - 255) * (255 * g - g^2) / 255^2 + g);
		} else {
			gVal =  (short) ((2 * rg - 255) * (Math.sqrt(255.0) * Math.sqrt((double)g) - g) / 255 + g);
		}
		
		if (b <= 127) {
			bVal = (short) ((2 * rb - 255) * (255 * b - b^2) / 255^2 + b);
		} else {
			bVal =  (short) ((2 * rb - 255) * (Math.sqrt(255.0) * Math.sqrt((double)b) - b) / 255 + b);
		}
		
		r = (short) (rVal * alpha / 255 + r* (255 - alpha) / 255);
		g = (short) (gVal * alpha / 255 + g * (255 - alpha) / 255);
		b = (short) (bVal * alpha / 255 + b * (255 - alpha) / 255);
	}
	
	

	

}
