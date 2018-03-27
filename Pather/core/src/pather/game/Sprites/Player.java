package pather.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import pather.game.Pather;
import pather.game.Screens.PlayScreen;

//This class contains all our functionality for the player character. Built upon an example by Brent Aureli

public class Player extends Sprite {
    //Enumerations for all possible player states. Change accordingly.
    public enum State{
        FALLING,
        JUMPING,
        STANDING,
        RUNNING,
        DEAD
    }
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    //Texture regions and animations of the player.
    private TextureRegion characterStand;
    private Animation<TextureRegion> characterRun;
    private TextureRegion characterJump;
    private TextureRegion characterDead;

    private boolean runningRight;
    private boolean playerIsDead;

    private float stateTimer;
    private PlayScreen screen;

    public Player(PlayScreen screen){
        this.world = screen.getWorld();
        this.screen = screen;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        //get run animation frames
        //All of our Texture Region definitions must be changed to reflect our character regions and sizes
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 12; i++){
            // i * x, where i equals the amount of our run frames and x equals the width of a single run frame
            frames.add(new TextureRegion(screen.getAtlas().findRegion("character_running"), i * 64, 0, 64, 64));
        }
        characterRun = new Animation<TextureRegion>(0.1f, frames);

        characterJump = new TextureRegion(screen.getAtlas().findRegion("character_running"), 320, 0, 64, 64);
        characterDead = new TextureRegion(screen.getAtlas().findRegion("character_running"), 384, 0, 64, 64);
        //create texture regions for Player standing
        characterStand = new TextureRegion(screen.getAtlas().findRegion("character_standing"), 0,0, 64, 64);

        //define player in box2d
        definePlayer();
        //set initial values for mario's location, width and height
        setBounds(0, 0, 64 / Pather.PPM, 64 / Pather.PPM);
        setRegion(characterStand);
    }

    public void update(float dt){
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + 2 / Pather.PPM);
        setRegion(getFrame(dt));
        if(b2body.getPosition().y < 0 && !playerIsDead) { //You die if you fall off the map
            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0);
            kill();
        }
    }

    //This method tells us which of our textures to use according to what state the player is in
    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case DEAD:
                region = characterDead;
                break;
            case JUMPING:
                region = characterJump;
                break;
            case RUNNING:
                region = characterRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = characterStand;
                break;
        }

        //This is used to flip the player sprite according to which direction it is running
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(playerIsDead){
            return State.DEAD;
        }
        else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)){
            return State.JUMPING;
        }else if(b2body.getLinearVelocity().y < 0){
            return State.FALLING;
        }else if(b2body.getLinearVelocity().x != 0){
            return State.RUNNING;
        }else{
            return State.STANDING;
        }
    }

    public boolean isDead(){
        return playerIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    //Define what happens when our character hits an enemy
    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }else{
	    kill();
        }
    }

    public void kill() {
        Pather.manager.get("audio/sounds/playerIsKill.wav", Sound.class).play();
        playerIsDead = true;
        Filter filter = new Filter();
        filter.maskBits = Pather.NOTHING_BIT;
        for (Fixture fixture : b2body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0f);
        b2body.applyLinearImpulse(new Vector2(b2body.getLinearVelocity().x, 10f), b2body.getWorldCenter(), true);
    }

    public void useItem(){
        screen.setGravity(-10);
    }


    //This is needed
    public void definePlayer(){
        //body definitions
        BodyDef bdef = new BodyDef();
        bdef.position.set(64 / Pather.PPM, 64 / Pather.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        //collision definitions
        FixtureDef fdef = new FixtureDef();

        CircleShape shape = new CircleShape();
        shape.setRadius(14 / Pather.PPM);
        shape.setPosition(new Vector2(0, -16 / Pather.PPM));


        fdef.filter.categoryBits = Pather.PLAYER_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                Pather.DANGERZONE_BIT |
                Pather.WIN_BIT |
                Pather.ENEMY_BIT |
                Pather.OBJECT_BIT |
                Pather.ENEMY_HEAD_BIT |
                Pather.ITEM_BIT;
		
        fdef.shape = shape;
        fdef.friction = 0.05f;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, 16 / Pather.PPM));
        b2body.createFixture(fdef).setUserData(this);

            //Give the player feet
            EdgeShape feet = new EdgeShape();
            feet.set(new Vector2(-14 / Pather.PPM, -30 / Pather.PPM), new Vector2(14 / Pather.PPM, -30 / Pather.PPM));
            fdef.filter.categoryBits = Pather.PLAYER_BIT;
        fdef.filter.maskBits = Pather.GROUND_BIT |
                Pather.WIN_BIT |
                Pather.OBJECT_BIT |
                Pather.ITEM_BIT;

            fdef.shape = feet;
        fdef.friction = 0.1f;

            b2body.createFixture(fdef).setUserData(this);

        //Our character has a small line above head so that it can hit objects with its head
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Pather.PPM, 6 / Pather.PPM), new Vector2(2 / Pather.PPM, 6 / Pather.PPM));
        fdef.filter.categoryBits = Pather.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }
}
