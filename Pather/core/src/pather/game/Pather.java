package pather.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pather.game.Screens.MainMenuScreen;


public class Pather extends Game {
	//Parameters for view dimensions and pixel per meter
	public static final int V_WIDTH = 768;
	public static final int V_HEIGHT = 432;
	public static final float PPM = 32f;

	//Bits to detect collisions, delete and add new ones as needed. The bits need to be n^2 numbers
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short WIN_BIT = 4;
	public static final short DANGERZONE_BIT = 8;
	public static final short OBJECT_BIT = 16;
	public static final short ENEMY_BIT = 32;
	public static final short ENEMY_HEAD_BIT = 64; //Never used, but some legacy classes require this
	public static final short ITEM_BIT = 128;
	public static final short PLAYER_HEAD_BIT = 256;

	public SpriteBatch batch;
	public static AssetManager manager;
	public MainMenuScreen mainMenuScreen;
	public static boolean toggleSound = true;

	@Override
	public void create () {
		if(Gdx.files.local("generated.tmx").exists()) Gdx.files.local("generated.tmx").delete();

		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/sounds/playerIsKill.wav", Sound.class);
		manager.load("audio/sounds/jump.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/powerup.wav", Sound.class);
		manager.load("audio/music/background_music.wav", Music.class);

		manager.finishLoading();
		mainMenuScreen = new MainMenuScreen(this);
		setScreen(mainMenuScreen);
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
