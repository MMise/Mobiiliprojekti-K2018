package pather.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.text.DecimalFormat;

import pather.game.Pather;

//This is an example on how to build a HUD that appears on the top of the screen

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;
    public static final float TIME_TO_CLEAR_LEVEL = 120;

    private float time;

    private boolean timerIsRunning;


    Label countdownLabel;
    Label timeLabel;

    public Hud(SpriteBatch sb){
        time = TIME_TO_CLEAR_LEVEL;
        timerIsRunning = true;
        viewport = new FitViewport(Pather.V_WIDTH, Pather.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        //Labels with dynamic numeric information
        countdownLabel = new Label(String.format("%.2f", time), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        //Labels with pure text information
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(timeLabel).expandX().padTop(10);
        //Add a second row
        table.row();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }

    public void update(float dt){
        if(timerIsRunning) {
            time -= dt;
        }else{
            time = 0;
        }
        countdownLabel.setText(new DecimalFormat("000.00").format(time));
    }

    public float getTime() { return time; }

    public void stopTimer(){
        timerIsRunning = false;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    }
