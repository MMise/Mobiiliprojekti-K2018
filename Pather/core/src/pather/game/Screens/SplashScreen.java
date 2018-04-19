package pather.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pather.game.Pather;

public class SplashScreen implements Screen {

    private Viewport viewport;
    private Stage stage;
    private float timer;
    private Game game;
    private float alphaValue = 0;
    private final Image patherLogo;

    public SplashScreen(final Game game) {
        this.game = game;
        viewport = new FitViewport(Pather.V_WIDTH, Pather.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((Pather) game).batch);

        patherLogo = new Image(new Texture(Gdx.files.internal("pather.png")));
        patherLogo.setPosition((Pather.V_WIDTH - patherLogo.getWidth())/2,(Pather.V_HEIGHT - patherLogo.getHeight()) / 2);
        patherLogo.setColor(1,1,1,0);

        stage.addActor(patherLogo);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        timer += delta;
        if(timer >= 5f) { //this screen is visible for three seconds
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
        alphaValue += 0.003f;
        patherLogo.setColor(1,1,1,alphaValue);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
