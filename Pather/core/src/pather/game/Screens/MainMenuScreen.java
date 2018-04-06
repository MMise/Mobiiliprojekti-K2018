package pather.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.ArrayList;
import java.util.Collections;

import pather.game.Pather;


public class MainMenuScreen implements Screen {

    private Viewport viewport;
    private Stage stage;
    private Game game;
    private final float scale = 1f;
    private final float padX = 60;

    private final Image playButton;
    private final Image shopButton;
    private final Image exitButton;

    private Screen loadingScreen;

    public MainMenuScreen(final Game game){

        this.game = game;
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        viewport = new FitViewport(Pather.V_WIDTH, Pather.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((Pather) game).batch);
        Gdx.input.setInputProcessor(stage);

        //Initialize button images
        playButton = new Image(new Texture(Gdx.files.internal("pather_menu_play.png")));
        shopButton = new Image(new Texture(Gdx.files.internal("pather_menu_shop.png")));
        exitButton = new Image(new Texture(Gdx.files.internal("pather_menu_exit.png")));

        //Set button scale
        /*
        playButton.setScale(scale);
        shopButton.setScale(scale);
        exitButton.setScale(scale);
        */

        //Set positions
        playButton.setPosition(padX, Pather.V_HEIGHT - playButton.getHeight());
        shopButton.setPosition(padX, Pather.V_HEIGHT - (playButton.getHeight() * 2));
        exitButton.setPosition(padX, Pather.V_HEIGHT - (playButton.getHeight() * 3));


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

        shopButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                game.setScreen(new EditorScreen((Pather) game));
            }
        });

        stage.addActor(playButton);
        stage.addActor(shopButton);
        stage.addActor(exitButton);

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

    public void showShop(){
        final Image shopWindow = new Image(new Texture(Gdx.files.internal("1cc.png")));
        shopWindow.setScale(0.5f);
        shopWindow.setPosition((Pather.V_WIDTH - shopWindow.getWidth() * 0.5f) / 2, 0);

        shopWindow.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                hideShop();
                shopButton.setVisible(true);
            }
        });
        stage.addActor(shopWindow);
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
