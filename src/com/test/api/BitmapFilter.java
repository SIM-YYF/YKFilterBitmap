package com.test.api;

import android.graphics.Color;

public class BitmapFilter {
	
	

	public static void contrast(int[] pixels, int[] des, int width, int height,
			int degree) {
		double contrast = (100.0 + degree) / 100.0;
		contrast *= contrast;
		
		int size = width * height;
		short r, g, b, alpha;
		double rr, gg, bb;
		for (int i = 0; i < size; i++) {
			alpha = (short) Color.alpha(pixels[i]);
			r = (short) Color.red(pixels[i]);
			g = (short) Color.green(pixels[i]);
			b = (short) Color.blue(pixels[i]);

			rr = (((r / 255.0 - 0.5) * contrast + 0.5) * 255);
			gg = (((g / 255.0 - 0.5) * contrast + 0.5) * 255);
			bb = (((b / 255.0 - 0.5) * contrast + 0.5) * 255);
			
			
			if (rr < 0.0)
				rr = 0;
			if (rr > 255.0)
				rr = 255;
			if (gg < 0.0)
				gg = 0;
			if (gg > 255.0)
				gg = 255;
			if (bb < 0.0)
				bb = 0;
			if (bb > 255.0)
				bb = 255;
			r = (short) rr;
			g = (short) gg;
			b = (short) bb;
			des[i] = Color.argb(alpha, r, g, b);
		}

	}

	
	
	/**
	 * 黑白
	 * @param src
	 * @param des
	 * @param width
	 * @param height
	 */
	public static void filter_black(int[] src, int[] des, int width, int height){
		short alpha, red, green, blue;
		short gray;
		for(int i = 0; i < src.length; i++){
			alpha = (short) Color.alpha(src[i]);
			red = (short) Color.red(src[i]);
			green = (short) Color.green(src[i]);
			blue = (short) Color.blue(src[i]);
			
			//计算灰色色彩
			gray = (short) ((red + green + blue) / 3);
			
			red = gray;
			green = gray;
			blue = gray;
			
			des[i] = Color.argb(alpha, red, green, blue);
		}
		
	}
	
	/**
	 * 锐化
	 * @param src
	 * @param des
	 * @param width
	 * @param height
	 */
	public static void filter_sharpen(int[] src, int[] des, int width, int height) {
		//对比度
		//degree取值范围在[-100 , 100]
		contrast(src, des, width, height, 52);
		int size = width * height;
		short r,g,b;
		for (int i = 0; i < size; i++) {
			r = (short) Color.red(des[i]);
			g = (short) Color.green(des[i]);
			b = (short) Color.blue(des[i]);

			//柔光模式下混合
			MMD.light_alpha(r, g, b, (short) 255, (short) 255, (short) 255,(short) 217);
			//柔光模式下混合
			MMD.light_alpha(r, g, b, (short) 255, (short) 202, (short) 186,(short) 123);
			des[i] = Color.argb((short) Color.alpha(des[i]), r, g,b);
		}
	}

	
	// 阳光Sunshine
		public static void filter_sunshine(int[] src, int[] des, int[] mask,
				int width, int height, int mask_width, int mask_height) {
			contrast(src, des, width, height, 52);
			Myshort r = new Myshort(), g = new Myshort(), b = new Myshort();
			short mask_r, mask_g, mask_b;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int pos = y * width + x;

					r.value = (short) Color.red(des[pos]);
					g.value = (short) Color.green(des[pos]);
					b.value = (short) Color.blue(des[pos]);

					//在柔光模式下进行叠加或者混合(rgb:255, 255, 255)
					MixedMode.soft_light(r, g, b, (short) 255, (short) 255, (short) 255);
					//在绿光 + Alpha 模式下进行叠加或者混合(rgba: 143, 27, 147,94)
					MixedMode.sreen_alpha(r, g, b, (short) 143, (short) 27, (short) 147,
							(short) 94);
					//在柔光 + Alpha 模式进行叠加或者混合(rgba:199,169,31,102) 
					MixedMode.soft_light_alpha_2(r, g, b, (short) 199, (short) 169, (short) 31,
							(short) 102);
					if (x < mask_width && y < mask_height) {

						int temp = y * mask_width + x;
						mask_r = (short) Color.red(mask[temp]);
						mask_g = (short) Color.green(mask[temp]);
						mask_b = (short) Color.blue(mask[temp]);

						MixedMode.screen(r, g, b, mask_r, mask_g, mask_b);
						MixedMode.screen(r, g, b, mask_r, mask_g, mask_b);
					}

					des[pos] = Color.argb((short) Color.alpha(des[pos]), r.value,
							g.value, b.value);
				}
			}
		}

	

}
