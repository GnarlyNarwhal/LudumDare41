package com.gnarly.engine.texture;

public class Anim extends Texture {

	private final float FRAME_WIDTH;
	private final long NANO_PER_FRAME;
	private final int NUM_FRAMES;
	
	private int curFrame;
	private long startTime;
	private boolean playing;
	
	public Anim(String path, int numFrames, int fps) {
		super(path);
		FRAME_WIDTH = 1f / (float) numFrames;
		NANO_PER_FRAME = 1000000000l / fps;
		this.curFrame = 0;
		this.NUM_FRAMES = numFrames;
		startTime = System.nanoTime();
		playing = true;
	}
	
	@Override
	public void bind() {
		super.bind();
		if(playing) {
			int frame = (int) ((System.nanoTime() - startTime) / NANO_PER_FRAME);
			curFrame = frame % NUM_FRAMES;
		}
	}
	
	public void play() {
		startTime = System.nanoTime();
		playing = true;
	}
	
	public void pause() {
		playing = false;
	}
	
	@Override
	public int getWidth() {
		return width / NUM_FRAMES;
	}
	
	public float getFrameWidth() {
		return FRAME_WIDTH;
	}
	
	public float getOffset() {
		return FRAME_WIDTH * curFrame;
	}
	
	public void setFrame(int frame) {
		curFrame = frame;
	}
}
