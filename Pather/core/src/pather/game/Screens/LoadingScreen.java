package pather.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pather.game.Pather;
import pather.game.Tools.MapEncoder;

//Screen shown after the levels have been chosen and get sent to the encoder

public class LoadingScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private Image logo;
    private float x, y, barWidth, barHeight, barProgress = 0f;
    private int length, progress = 0;
    private ShapeRenderer shapes;
    private MapEncoder encoder;
    private String[] maps;

    private Game game;

    public LoadingScreen(Game game, String[] str){
        this.game = game;
        viewport = new FitViewport(Pather.V_WIDTH, Pather.V_HEIGHT,  new OrthographicCamera());
        stage = new Stage(viewport, ((Pather) game).batch);

        logo = new Image(new Texture(Gdx.files.internal("Pather-logo.png")));
        logo.setPosition((Pather.V_WIDTH - logo.getWidth())/2,Pather.V_HEIGHT * 0.3f);

        x = Pather.V_WIDTH * 0.05f;
        y = Pather.V_HEIGHT * 0.1f;
        barWidth = Pather.V_WIDTH * 0.9f;
        barHeight = Pather.V_HEIGHT * 0.05f;

        maps = str;
        length = maps.length*2;

        shapes = new ShapeRenderer();
        shapes.setProjectionMatrix(viewport.getCamera().combined);

        encoder = new MapEncoder();
        for(String map : maps)
            encoder.decode(map);

        stage.addActor(logo);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(.38f, .42f, .42f, 1);
        shapes.rect(x, y, barWidth, barHeight);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(.38f, .42f, .42f, 1);
        shapes.rect(x, y, barProgress, barHeight);
        shapes.end();

        update();

        if (progress == length) game.setScreen(new PlayScreen((Pather) game));
        else encoder.encode();
    }

    public void update() {
        progress = encoder.getProgress();
        barProgress = (progress == 0) ? 0f : ((float)progress / length) * barWidth;
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
