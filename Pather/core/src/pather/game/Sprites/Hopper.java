package pather.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import pather.game.Pather;
import pather.game.Screens.PlayScreen;


/*
    This is an example based on the Goomba enemy in Super Mario Brothers, originally by Brent Aureli
    Our enemy can't be stomped on, instead touching it instantly kills the player
 */

public class Hopper extends Enemy {

    private float stateTime;
    private TextureRegion hopperTexture;
    private boolean setToDestroy;

    public Hopper(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        //Our enemy doesn't have a walk animation, this is just how we set its texture
        hopperTexture = new TextureRegion(screen.getAtlas().findRegion("hopper"), 0, 0, 32, 32);
        stateTime = 0;
        setBounds(getX(), getY(), 32 / Pather.PPM, 32 / Pather.PPM);
        setToDestroy = false;
        destroyed = false;
        this.velocity = new Vector2(0f, 13f);
    }

    public void update(float dt){
        stateTime += dt;
        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            stateTime = 0;
        }else if(!destroyed){
            if(stateTime > 5) {
                b2body.setLinearVelocity(velocity);
                stateTime = 0;
            }
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(hopperTexture);
        }
    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 1){
            super.draw(batch);
        }
    }

    @Override
    protected void defineEnemy() {
        //body definitions
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        //box2d definitions and collision definitions
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(16 / Pather.PPM);
        fdef.filter.categoryBits = Pather.ENEMY_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                                Pather.DANGERZONE_BIT |
                                Pather.WIN_BIT |
                                Pather.ENEMY_BIT |
                                Pather.OBJECT_BIT |
                                Pather.PLAYER_BIT;

        fdef.shape = shape;
        fdef.friction = 0f;
        fdef.density = 1f;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    @Override
    public void hitOnHead(Player player) {

    }

    public void onEnemyHit(Enemy enemy){
        reverseVelocity(true, false);
    }
}
