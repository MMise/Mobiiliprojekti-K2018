package com.mygdx.game.managers;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.states.GameState;
import com.mygdx.game.states.PlayState;
import com.mygdx.game.states.SplashState;

import java.util.Stack;

/**
 * Created by OMISTAJ on 15.3.2018.
 */

public class GameStateManager {

    //Application Reference
    private final MyGdxGame app;

    private Stack<GameState> states;

    public enum State{
        SPLASH,
        PLAY
    }

    public GameStateManager(final MyGdxGame app){
        this.app = app;
        this.states = new Stack<GameState>();
        this.setState(State.SPLASH);
    }

    public MyGdxGame application(){
        return app;
    }

    public void update(float delta){
        states.peek().update(delta);
    }

    public void render(){
        states.peek().render();
    }

    public void dispose(){
        for(GameState gs : states){
            gs.dispose();
        }
        states.clear();
    }

    public void resize(int w, int h){
        states.peek().resize(w, h);
    }

    public void setState(State state){
        if(states.size() >= 1){
            states.pop().dispose();
        }
        states.push(getState(state));
    }

    private GameState getState(State state){
        switch(state){
            case SPLASH: return new SplashState(this);
            case PLAY: return new PlayState(this);
        }
        return null;
    }
}
