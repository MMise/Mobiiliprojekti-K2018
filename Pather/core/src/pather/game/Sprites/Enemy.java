package pather.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import pather.game.Screens.PlayScreen;

/**
 * Created by OMISTAJ on 17.3.2018.
 */

//Abstract enemy class that implements all the basic functionality of enemies

public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(0, 0);
        b2body.setActive(false);
    }

    protected abstract void defineEnemy();
    public abstract void hitOnHead(Player player);
    public abstract void update(float dt);
    public abstract void onEnemyHit(Enemy enemy);

    public void reverseVelocity(boolean x, boolean y){
        /*if(x){
            velocity.x = -velocity.x;
        }
        if(y){
            velocity.y = -velocity.y;
        }*/
    }
}
