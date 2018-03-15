package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Controller {
    Viewport viewport;
    Stage stage;
    boolean leftPressed, rightPressed, upPressed;
    OrthographicCamera camera;

    public Controller(float screenWidth, float screenHeight){
        camera = new OrthographicCamera();
        viewport = new FitViewport(screenWidth, screenHeight, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        Image jumpButtonImage = new Image(new Texture(Gdx.files.internal("badlogic.jpg")));
        Image leftButtonImage = new Image(new Texture(Gdx.files.internal("left_arrow.png")));
        Image rightButtonImage = new Image(new Texture(Gdx.files.internal("right_arrow.png")));

        jumpButtonImage.setPosition(screenWidth - jumpButtonImage.getWidth(), 0);
        leftButtonImage.setPosition(0, 0);
        rightButtonImage.setPosition( rightButtonImage.getWidth(), 0);

        jumpButtonImage.addListener(new InputListener() {
           @Override
           public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                upPressed = true;
                return true;
           }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                upPressed = false;
            }
        });

        leftButtonImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                leftPressed = false;
            }
        });

        rightButtonImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                rightPressed = false;
            }
        });

        stage.addActor(jumpButtonImage);
        stage.addActor(rightButtonImage);
        stage.addActor(leftButtonImage);
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

    public boolean isUpPressed() {
        return upPressed;
    }

    public void resize(int width, int height){
        viewport.update(width, height);
    }
}
