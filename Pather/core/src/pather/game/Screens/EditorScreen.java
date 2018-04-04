package pather.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pather.game.Pather;

/**
 * Created by Mikko on 3.4.2018.
 */

public class EditorScreen implements Screen {

    private Viewport viewport;
    private Stage stage;
    private final Game game;
    private float timer;

    private SelectBox selectBox1, selectBox2, selectBox3;

    private Skin skin;
    private String module1, module2, module3;
    private final Image playButton;
    private LoadingScreen loadingScreen;

    public EditorScreen(Game game){
        this.game = game;

        FileHandle dir = Gdx.files.internal("maps/");
        String[] mapList = new String[dir.list().length];
        for(int i = 0; i<dir.list().length; i++){
            mapList[i] = dir.list()[i].name();
        }

        viewport = new FitViewport(Pather.V_WIDTH / 2, Pather.V_HEIGHT / 2, new OrthographicCamera());
        stage = new Stage(viewport, ((Pather) game).batch);
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.WHITE);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        playButton = new Image(new Texture(Gdx.files.internal("pather_menu_play.png")));
        playButton.setScale(0.5f);
        playButton.setPosition(viewport.getWorldWidth() / 2 - playButton.getWidth() / 4, viewport.getWorldHeight() * 0.01f);

        selectBox1 = new SelectBox(skin);
        selectBox1.setItems(mapList);
        selectBox1.setPosition(viewport.getWorldWidth() * 0.078125f, viewport.getWorldHeight() * 0.3f);
        selectBox1.setWidth(80f);

        selectBox2 = new SelectBox(skin);
        selectBox2.setItems(mapList);
        selectBox2.setWidth(80f);
        selectBox2.setPosition(viewport.getWorldWidth() / 2 - selectBox2.getWidth() / 2, viewport.getWorldHeight() * 0.3f);

        selectBox3 = new SelectBox(skin);
        selectBox3.setItems(mapList);
        selectBox3.setPosition(viewport.getWorldWidth() / 1.4f, viewport.getWorldHeight() * 0.3f);
        selectBox3.setWidth(80f);

        module1 = (String) selectBox1.getSelected();
        module2 = (String) selectBox2.getSelected();
        module3 = (String) selectBox3.getSelected();

        selectBox1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                module1 = (String) selectBox1.getSelected();
                System.out.println(module1);
            }
        });

        selectBox2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                module2 = (String) selectBox2.getSelected();
                System.out.println(module2);
            }
        });

        selectBox3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                module3 = (String) selectBox3.getSelected();
                System.out.println(module3);
            }
        });

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

        stage.addActor(selectBox1);
        stage.addActor(selectBox2);
        stage.addActor(selectBox3);
        stage.addActor(playButton);

        Gdx.app.log("Editor", String.valueOf(selectBox3.getX()));
        Gdx.app.log("Editor", String.valueOf(selectBox3.getWidth()));
    }

    public void play(){
        loadingScreen = new LoadingScreen((Pather) game, new String[] { module1, module2, module3 });
        game.setScreen(loadingScreen);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        timer+=delta;
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
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
