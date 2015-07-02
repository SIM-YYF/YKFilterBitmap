package com.test.api;

public class MMD {

	
	/**
	 * 柔光混合模式
	 * B <= 127
	 * A = (((2 * B) - 1) * (1 * A - A^2)/1^2 + A)
	 * B > 127
	 * A = ((2 * B) - 1) * (sqrt(1) * sqrt(A) - A) / 1 + A
	 * @param r
	 * @param g
	 * @param b
	 * @param req_r
	 * @param req_g
	 * @param req_b
	 */
	public static void light(short r, short g, short b, short rr, short rg, short rb){
		if(rr <= 127){
			r = (short)(((2 * rr) - 255) * (255 * r - r^2) / 255^2 + r);
		}else{
			r = (short)(((2 * rr) - 255) * (Math.sqrt(255) * Math.sqrt(r) - r) / 255 + r);
		}
		
		if(rg <= 127){
			g = (short)(((2 * rg) - 255) * (255 * g - g^2) / 255^2 + g);
		}else{
			g = (short)(((2 * rg) - 255) * (Math.sqrt(255) * Math.sqrt(g) - g) / 255 + g);
		}
		
		if(rb <= 127){
			b = (short)(((2 * rb) - 255) * (255 * b - b^2) / 255^2 + b);
		}else{
			b = (short)(((2 * rb) - 255) * (Math.sqrt(255) * Math.sqrt(b) - b) / 255 + b);
		}
		
	}
	/**
	 * 柔光  + alpha 混合模式
	 * B <= 127
	 * T = (((2 * B) - 1) * (1 * A - A^2)/1^2 + A)
	 * B > 127
	 * T = (((2 * B) - 1) * (sqrt(1) * sqrt(A) - A) / 1 + A) 
	 * 
	 * 
	 * A = T * alpha / 1 + A * (1 - alpha) / 1
	 * 
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	public static void light_alpha(short r, short g, short b,short rr, short rg, short rb, short alpha) {
		short rVal, gVal, bVal;

		if (r <= 127) {
			rVal = (short) ((2 * rr - 255) * (255 * r - r^2) / 255^2 + r) ;
		} else {
			rVal =  (short) ((2 * rr - 255) * (Math.sqrt(255.0) * Math.sqrt((double)r) - r) / 255 + r);
		}
		
		if (g <= 127) {
			gVal = (short) ((2 * rg - 255) * (255 * g - g^2) / 255^2 + g) ;
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
	
	
	
	/**
	 * 混合模式为：绿色 
	 * 公式：A = (1-(((1-A)*(1-B)) << 8))
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	public static void Screen(short r, short g, short b,short rr, short rg, short rb) {
		r = (short) (255 - (((255 - r) * (255 - rr)) >> 8));
		g = (short) (255 - (((255 - g) * (255 - rg)) >> 8));
		b = (short) (255 - (((255 - b) * (255 - rb)) >> 8));
	}

	/**
	 * 混合模式：绿色 + alpha
	 * 公式：
	 * T = (1-((1-A)*(1-B) / 2 >> 8))
	 * A = T * a / 1 + A * (255 - a) / 1
	 * 
	 * 
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	public static void Screen(short r, short g, short b,short rr, short rg, short rb, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) (255 - (((255 - r) * (255 - rr)) >> 8));
		gVal = (short) (255 - (((255 - g) * (255 - rg)) >> 8));
		bVal = (short) (255 - (((255 - b) * (255 - rb)) >> 8));
		
		r = (short) (rVal * alpha / 255 + r * (255 - alpha) / 255);
		g = (short) (gVal * alpha / 255 + g * (255 - alpha) / 255);
		b = (short) (bVal * alpha / 255 + b * (255 - alpha) / 255);
	}

	
	
}
