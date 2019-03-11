package com.gnarly.engine.texture;

public class Spritesheet extends Texture {

	int fWidth, fHeight;
	
	public Spritesheet(int fWidth, int fHeight, String path) {
		super(path);
		this.fWidth = fWidth;
		this.fHeight = fHeight;
	}

	@Override
	public int getWidth() {
		return width / fWidth;
	}
	
	@Override
	public int getHeight() {
		return height / fHeight;
	}
	
	public float getFWidth() {
		return 1f / (float) fWidth;
	}
	
	public float getFHeight() {
		return 1f / (float) fHeight;
	}
}
