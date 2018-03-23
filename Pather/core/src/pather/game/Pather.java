package pather.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pather.game.Screens.PlayScreen;


public class Pather extends Game {
	//Parameters for view dimensions and pixel per meter
	public static final int V_WIDTH = 768;
	public static final int V_HEIGHT = 432;
	public static final float PPM = 32f;
	public static final float SCALE = 2f;

	//Bits to detect collisions, delete and add new ones as needed. The bits need to be n^2 numbers
	public static final short NOTHING_BIT = 0; //
	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short WIN_BIT = 4;
	public static final short DANGERZONE_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short TRIGGER_BIT = 1024;


	public SpriteBatch batch;
	public static AssetManager manager;

	@Override
	public void create () {
		if(Gdx.files.local("temp.tmx").exists()) Gdx.files.local("temp.tmx").delete();

		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/sounds/playerIsKill.wav", Sound.class);
		//This is how you load sound files into memory
		/*
		manager.load("path/to/music.ogg", Music.class);
		manager.load("path/to/audio.wav", Sound.class);
		*/
		manager.finishLoading();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();

	}

	@Override
	public void dispose () {
		super.dispose();
		manager.dispose();
		batch.dispose();
	}
}
