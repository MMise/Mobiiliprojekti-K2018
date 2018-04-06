package pather.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

/*
This is a generic screen that's shown whenever the player dies or clears a level.
The shown message must be defined whenever creating a screen
*/

public class LabelScreen implements Screen {

    private Viewport viewport;
    private Stage stage;
    private Label playAgain;
    private final Game game;
    private float timer;

    public LabelScreen(Game game, String labelText, float time){
        this.game = game;
        viewport = new FitViewport(Pather.V_WIDTH, Pather.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((Pather) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label(labelText + " Time: "+ String.format("%.2f", time), font);
        playAgain = new Label("TAP TO PLAY AGAIN", font);
        playAgain.setVisible(false);
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
        if(timer >= 3f) { //wait three seconds to prevent player from accidentally tapping out
            playAgain.setVisible(true);
            if(Gdx.input.isTouched()){
                game.setScreen(new PlayScreen((Pather) game));
                dispose();
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            game.setScreen(new MainMenuScreen(game));
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
