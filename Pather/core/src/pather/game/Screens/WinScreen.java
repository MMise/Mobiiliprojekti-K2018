package pather.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.text.DecimalFormat;

import pather.game.Pather;

/**
 * Created by Mikko on 22.3.2018.
 */

public class WinScreen implements Screen {

    private Viewport viewport;
    private Stage stage;
    private final Game game;
    private float timer;

    public WinScreen(Game game, float time){
        this.game = game;

        viewport = new FitViewport(Pather.V_WIDTH, Pather.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((Pather) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("YOU WIN! Time: "+ String.format("%.2f", time), font);
        Label playAgain = new Label("TAP TO PLAY AGAIN", font);
        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgain).expandX().padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        timer+=delta;
        if(Gdx.input.isTouched() && timer >= 3f){
            game.setScreen(new PlayScreen((Pather) game));
            dispose();
        }
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
