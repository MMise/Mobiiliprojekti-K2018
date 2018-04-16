package pather.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Collections;

import pather.game.Pather;


public class MainMenuScreen implements Screen {

    private Viewport viewport;
    private Stage stage;
    private Game game;
    private final float padX = 20;
    private final float padY = 10f;

    private final Image playButton;
    private final Image editButton;
    private final Image exitButton;
    private final Image shopButton;

    private final Image soundOnButton;
    private final Image soundOffButton;
    public boolean soundIsOn = true;

    private Screen loadingScreen;

    public MainMenuScreen(final Game game){

        this.game = game;
        Table table = new Table();
        table.top();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("menu_art.png")))));
        table.setFillParent(true);
        viewport = new FitViewport(Pather.V_WIDTH, Pather.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((Pather) game).batch);
        Gdx.input.setInputProcessor(stage);

        //Initialize button images
        playButton = new Image(new Texture(Gdx.files.internal("pather_menu_play.png")));
        editButton = new Image(new Texture(Gdx.files.internal("pather_menu_edit.png")));
        exitButton = new Image(new Texture(Gdx.files.internal("pather_menu_exit.png")));
        shopButton = new Image(new Texture(Gdx.files.internal("pather_menu_shop.png")));

        soundOnButton = new Image(new Texture(Gdx.files.internal("sound_on.png")));
        soundOffButton = new Image(new Texture(Gdx.files.internal("sound_off.png")));

        //Set positions
        playButton.setPosition(padX, Pather.V_HEIGHT - playButton.getHeight());
        editButton.setPosition(padX, Pather.V_HEIGHT - (playButton.getHeight() * 1.8f));
        shopButton.setPosition(padX, Pather.V_HEIGHT - (playButton.getHeight() * 2.6f));
        exitButton.setPosition(padX, Pather.V_HEIGHT - (playButton.getHeight() * 3.4f));
        soundOnButton.setPosition(Pather.V_WIDTH - soundOnButton.getWidth(), padY);
        soundOffButton.setPosition(soundOnButton.getX(), soundOnButton.getY());
        soundOffButton.setVisible(false);

        if(Pather.toggleSound){
            soundOnButton.setVisible(true);
            soundOffButton.setVisible(false);
        }
        else{
            soundOnButton.setVisible(false);
            soundOffButton.setVisible(true);
        }


        playButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                play();
            }
        });

        exitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                Gdx.app.exit();
            }
        });

        editButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                game.setScreen(new EditorScreen((Pather) game));
                dispose();
            }
        });

        shopButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //TODO IMPLEMENT NEW SHOP SCREEN
            }
        });

        soundOnButton.addListener(new InputListener() {
           @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
               return true;
           }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                soundOnButton.setVisible(false);
                soundOffButton.setVisible(true);
                soundIsOn = false;
                Pather.toggleSound = !Pather.toggleSound;
            }
        });

        soundOffButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                soundOnButton.setVisible(true);
                soundOffButton.setVisible(false);
                //soundIsOn = true;
                Pather.toggleSound = !Pather.toggleSound;

            }
        });

		stage.addActor(table);
        stage.addActor(playButton);
        stage.addActor(editButton);
        stage.addActor(exitButton);
        stage.addActor(shopButton);
        stage.addActor(soundOnButton);
        stage.addActor(soundOffButton);
    }


    public void play(){ //Generate a random stage
        FileHandle[] dir = Gdx.files.internal("maps/").list();
        ArrayList<String> maps = new ArrayList<String>();
        int length = dir.length;
        String[] result = new String[Math.min(3, length)];
        for(FileHandle item : dir)
            maps.add(item.name());
        Collections.shuffle(maps);
        for(int i = 0; i < result.length; i++) {
            result[i] = maps.get(i).substring(0, maps.get(i).length() - 4);
        }
        loadingScreen = new LoadingScreen((Pather) game, result);
        game.setScreen(loadingScreen);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            Gdx.app.exit();
        }

        Gdx.gl.glClearColor(0,0.5f,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }


    public void hideShop(){
        stage.getActors().removeIndex(3);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
