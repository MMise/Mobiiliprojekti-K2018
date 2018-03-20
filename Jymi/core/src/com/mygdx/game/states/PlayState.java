package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.game.managers.GameStateManager;
import com.mygdx.game.utils.TiledUtil;

import static com.mygdx.game.utils.Constants.PPM;

public class PlayState extends GameState {

    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;
    private Box2DDebugRenderer b2dr;
    private World world;
    private Body player, platform, body1, body2, body3;

    private Texture tex;

    public PlayState(GameStateManager gsm){

        super(gsm);
        world = new World(new Vector2(0, -9.8f), false);
        b2dr = new Box2DDebugRenderer();
        player = createBox(140, 140, 32,32, false, false);
        platform = createBox(140, 130, 64, 32, true, false);

        tex = new Texture("Images/cat.png");
        map = new TmxMapLoader().load("hell.tmx");
        tmr = new OrthogonalTiledMapRenderer(map);

        TiledUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
    }

    @Override
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);
        inputUpdate(delta);
        cameraUpdate();
        tmr.setView(camera);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(tex, player.getPosition().x * PPM - (tex.getWidth() / 2), player.getPosition().y * PPM - (tex.getHeight()) / 2);
        batch.end();

        tmr.render();

        b2dr.render(world, camera.combined.scl(PPM));
    }

    @Override
    public void dispose() {
        b2dr.dispose();
        world.dispose();
        tmr.dispose();
        map.dispose();
    }

    public void inputUpdate(float delta){
        int horizontalForce = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            horizontalForce -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            horizontalForce += 1;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            player.applyForceToCenter(0, 30, false);
        }

        player.setLinearVelocity(horizontalForce * 5, player.getLinearVelocity().y);
    }

    public void cameraUpdate(){
        Vector3 position = camera.position;
        // Linear interpolation = a + (b - a) * lerp
        // a = current camera position
        // b = target
        // lerp = interpolation factor
        position.x = camera.position.x + (player.getPosition().x * PPM - camera.position.x) * .1f;
        position.y = camera.position.y + (player.getPosition().y * PPM - camera.position.y) * .1f;

        camera.position.set(position);
        camera.update();
    }

    private void initBodies(){
        body1 = createBox(140,140,32,32,true,false);
        body2 = createCircle(140, 140, 12, false);
        //body3 = createCircle(140,120,12, false);

        RevoluteJointDef rDef = new RevoluteJointDef();
        rDef.bodyA = body1;
        rDef.bodyB = body2;
        rDef.collideConnected = false;
        rDef.localAnchorA.set(0, 32 / PPM);
        rDef.localAnchorB.set(32 / PPM, 0);
        world.createJoint(rDef);
    }

    public Body createBox(int x, int y, int width, int height, boolean isStatic, boolean fixedRotation){
        Body pBody;
        BodyDef def = new BodyDef();
        if(isStatic){
            def.type = BodyDef.BodyType.StaticBody;
        }else{
            def.type = BodyDef.BodyType.DynamicBody;
        }

        def.position.set(x / PPM, y / PPM);
        def.fixedRotation = true;
        pBody = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM,width / 2 / PPM);

        pBody.createFixture(shape, 1.0f);
        shape.dispose();
        return pBody;
    }

    private Body createCircle(int x, int y, int radius, boolean isStatic) {
        Body cBody;
        BodyDef def = new BodyDef();
        if(isStatic){
            def.type = BodyDef.BodyType.StaticBody;
        }else{
            def.type = BodyDef.BodyType.DynamicBody;
        }
        def.position.set(x / PPM, y / PPM);
        cBody = world.createBody(def);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / PPM);
        cBody.createFixture(shape, 1.0f);
        shape.dispose();
        return cBody;
    }
}
