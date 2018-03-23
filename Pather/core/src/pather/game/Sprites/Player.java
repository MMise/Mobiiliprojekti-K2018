package pather.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
        GROWING,
        DEAD
    }
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    //Texture regions and animations of the player.
    //TODO: Refactor variable and method names to reflect our game
    private TextureRegion characterStand;
    private Animation<TextureRegion> characterRun;
    private TextureRegion characterJump;
    private TextureRegion characterDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;

    private float stateTimer;

    public Player(PlayScreen screen){
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        //get run animation frames
        //All of our Texture Region definitions must be changed to reflect our character regions and sizes
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++){
            // i * x, where i equals the amount of our run frames and x equals the width of a single run frame
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        characterRun = new Animation<TextureRegion>(0.1f, frames);
        //clear frames for next animation sequence
        frames.clear();
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //set animation frames for growing mario
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        characterJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        characterDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        //create texture regions for Player standing
        characterStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0,0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        //define mario in box2d
        defineMario();
        //set initial values for mario's location, width and height
        setBounds(0, 0, 32 / Pather.PPM, 32 / Pather.PPM);
        setRegion(characterStand);
    }

    public void update(float dt){
        if(marioIsBig){
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / Pather.PPM);
        }else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        setRegion(getFrame(dt));
        if(timeToDefineBigMario){
            defineBigMario();
        }
        if(timeToRedefineMario){
            redefineMario();
        }
        if(b2body.getPosition().y < 0 && !marioIsDead) { //You die if you fall off the map
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
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)){
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : characterJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : characterRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : characterStand;
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
        if(marioIsDead){
            return State.DEAD;
        }
        else if(runGrowAnimation){
            return State.GROWING;
        }else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)){
            return State.JUMPING;
        }else if(b2body.getLinearVelocity().y < 0){
            return State.FALLING;
        }else if(b2body.getLinearVelocity().x != 0){
            return State.RUNNING;
        }else{
            return State.STANDING;
        }
    }

    //This isn't probably needed in our game
    public void grow(){
        if(!isBig()) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            //Pather.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    //Define what happens when our character hits an enemy
    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }else{
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                //Pather.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                kill();
            }
        }
    }

    public void kill() {
        //Pather.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
        marioIsDead = true;
        Filter filter = new Filter();
        filter.maskBits = Pather.NOTHING_BIT;
        for (Fixture fixture : b2body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        b2body.applyLinearImpulse(new Vector2(10f, 10f), b2body.getWorldCenter(), true);
    }

    //following two methods probably aren't needed
    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / Pather.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(16 / Pather.PPM);
        fdef.filter.categoryBits = Pather.PLAYER_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                Pather.DANGERZONE_BIT |
                Pather.WIN_BIT |
                Pather.ENEMY_BIT |
                Pather.OBJECT_BIT |
                Pather.ENEMY_HEAD_BIT |
                Pather.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / Pather.PPM));
        b2body.createFixture(fdef).setUserData(this);

        //TODO attempting to mutate mario by giving him feet
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-16 / Pather.PPM, -20 / Pather.PPM), new Vector2(16 / Pather.PPM, -20 / Pather.PPM));
        fdef.filter.categoryBits = Pather.PLAYER_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                Pather.DANGERZONE_BIT |
                Pather.WIN_BIT |
                Pather.OBJECT_BIT |
                Pather.ITEM_BIT;

        fdef.shape = feet;

        b2body.createFixture(fdef).setUserData(this);
        // /TODO

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Pather.PPM, 6 / Pather.PPM), new Vector2(2 / Pather.PPM, 6 / Pather.PPM));
        fdef.filter.categoryBits = Pather.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(16 / Pather.PPM);
        fdef.filter.categoryBits = Pather.PLAYER_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                Pather.DANGERZONE_BIT |
                Pather.WIN_BIT |
                Pather.ENEMY_BIT |
                Pather.OBJECT_BIT |
                Pather.ENEMY_HEAD_BIT |
                Pather.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //TODO attempting to mutate mario by giving him feet
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-16 / Pather.PPM, -16 / Pather.PPM), new Vector2(16 / Pather.PPM, -16 / Pather.PPM));
        fdef.filter.categoryBits = Pather.PLAYER_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                Pather.DANGERZONE_BIT |
                Pather.WIN_BIT |
                Pather.OBJECT_BIT |
                Pather.ITEM_BIT;

        fdef.shape = feet;

        b2body.createFixture(fdef).setUserData(this);
        // /TODO

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Pather.PPM, 6 / Pather.PPM), new Vector2(2 / Pather.PPM, 6 / Pather.PPM));
        fdef.filter.categoryBits = Pather.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToRedefineMario = false;
    }

    //This is needed
    public void defineMario(){
        //body definitions
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Pather.PPM, 32 / Pather.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        //collision definitions
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(16 / Pather.PPM);
        fdef.filter.categoryBits = Pather.PLAYER_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                                Pather.DANGERZONE_BIT |
                                Pather.WIN_BIT |
                                Pather.ENEMY_BIT |
                                Pather.OBJECT_BIT |
                                Pather.ENEMY_HEAD_BIT |
                                Pather.ITEM_BIT;

        fdef.shape = shape;

        b2body.createFixture(fdef).setUserData(this);

        //TODO attempting to mutate mario by giving him feet
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-16 / Pather.PPM, -16 / Pather.PPM), new Vector2(16 / Pather.PPM, -16 / Pather.PPM));
        fdef.filter.categoryBits = Pather.PLAYER_BIT;
        fdef.filter.maskBits =  Pather.GROUND_BIT |
                Pather.WIN_BIT |
                Pather.OBJECT_BIT |
                Pather.ITEM_BIT;

        fdef.shape = feet;
        fdef.friction = 0.1f;

        b2body.createFixture(fdef).setUserData(this);
        // /TODO

        //Our character has a small line above head so that it can hit objects with its head
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Pather.PPM, 6 / Pather.PPM), new Vector2(2 / Pather.PPM, 6 / Pather.PPM));
        fdef.filter.categoryBits = Pather.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }
}
