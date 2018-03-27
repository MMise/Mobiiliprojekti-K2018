package pather.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import pather.game.Pather;
import pather.game.Screens.PlayScreen;


/**
 * Created by OMISTAJ on 17.3.2018.
 */

//This is an example on the Goomba enemy in Super Mario Brothers, originally by Brent Aureli

public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        //The goomba's walk animation is 2 frames in the example.
        for(int i = 0; i < 2; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }
        walkAnimation = new Animation<TextureRegion>(0.4f, frames); //each frame appears for 0.4 seconds
        stateTime = 0;
        setBounds(getX(), getY(), 16 / Pather.PPM, 16 / Pather.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt){
        stateTime += dt;
        if(setToDestroy && !destroyed){
            //If the enemy has been stomped on we change it's sprite to flat goomba
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        }else if(!destroyed){
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
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
        shape.setRadius(6 / Pather.PPM);
        fdef.filter.categoryBits = Pather.ENEMY_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                                Pather.DANGERZONE_BIT |
                                Pather.ENEMY_BIT |
                                Pather.OBJECT_BIT |
                                Pather.PLAYER_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Create the Head here. Our enemy has a small "platform" above it's box2d body.
        //If the player collides with the platform we know he came from above and thus stomped the enemy
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / Pather.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / Pather.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / Pather.PPM);
        vertice[3] = new Vector2(-3, 3).scl(1 / Pather.PPM);
        head.set(vertice);

        fdef.shape = head;
        //restitution = kimmmoisuus. More means the player bounces higher whenever he jumps on the enemy
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = Pather.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead(Player player) {
        //Pather.manager.get("audio/sounds/stomp.wav", Sound.class).play();
        setToDestroy = true;
    }

    public void onEnemyHit(Enemy enemy){
        //If the goomba collides with a moving turtle shell, it dies. Otherwise it reverses direction
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL){
            setToDestroy = true;
        }else{
            reverseVelocity(true, false);
        }
    }
}
