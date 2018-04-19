package pather.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.org.apache.bcel.internal.generic.NEW;

import static com.badlogic.gdx.Gdx.input;

public class Controller {
    private Viewport viewport;
    private Stage stage;
    private boolean leftPressed = false, rightPressed = false, upPressed = false, upHeld = false, exitPressed = false;
    private OrthographicCamera camera;
    private float scale;

    public Controller(float screenWidth, float screenHeight){
        camera = new OrthographicCamera();

        viewport = new FitViewport(screenWidth, screenHeight, camera);
        stage = new Stage(viewport);
        input.setInputProcessor(stage);
        input.setCatchBackKey(true);

        scale = screenWidth / 480;

        final Image input_jump = new Image(new Texture(Gdx.files.internal("jump.png")));
        final Image input_arrows = new Image(new Texture(Gdx.files.internal("arrows.png")));
        final Image input_exit = new Image(new Texture(Gdx.files.internal("pather_menu_exit_scaled.png")));
        input_arrows.setScale(scale);
        input_jump.setScale(scale);
        input_exit.setScale(scale);

        input_arrows.setPosition(screenWidth * 0.05f, screenHeight * 0.05f);
        input_jump.setPosition(screenWidth - input_jump.getWidth() * scale - screenWidth * 0.05f, screenHeight * 0.05f);
        input_exit.setPosition(screenWidth - input_exit.getWidth() * scale - screenWidth * 0.02f, screenHeight * 0.8f);

        input_jump.addListener(new InputListener() {
           @Override
           public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                upHeld = true;
                return true;
           }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                upHeld = upPressed = false;
            }
        });

        input_arrows.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                if(x < input_arrows.getWidth() / 2) {
                    leftPressed = true;
                } else {
                    rightPressed = true;
                }
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer){
                if(x < input_arrows.getWidth() / 2) {
                    leftPressed = true;
                    rightPressed = false;
                } else {
                    rightPressed = true;
                    leftPressed = false;
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                leftPressed = false;
                rightPressed = false;
            }
        });

        input_exit.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                exitPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                exitPressed = false;
            }
        });

        stage.addActor(input_jump);
        stage.addActor(input_arrows);
        stage.addActor(input_exit);
    }

    public void draw(){
        stage.act();
        stage.draw();
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isExitPressed(){return exitPressed;}

    public boolean isUpPressed() {
        if(upHeld && !upPressed) return (upPressed = true);
        else return false;
    }

    public void resize(int width, int height){
        viewport.update(width, height);
    }
}
