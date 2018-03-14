package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.utils.TiledUtil;

import static com.mygdx.game.utils.Constants.PPM;

public class MyGdxGame extends ApplicationAdapter {
	private boolean DEBUG = false;

	private OrthographicCamera camera;

	private Box2DDebugRenderer b2dr;
	private World world;
	private Body player;
	private Body platform;

	private OrthogonalTiledMapRenderer tiledMapRenderer;
	private OrthogonalTiledMapRenderer tiledMapRenderer2;
	private TiledMap map;
	private TiledMap map2;
	//private TiledMapTileSet tileset;


    float width;
	float height;
	@Override
	public void create () {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width / 2, height / 2);

		world = new World(new Vector2(0, -9.8f), false);
        player = createBox(20,60,32, 32, false);
        platform = createBox(0,-20,256,32,true);
		b2dr = new Box2DDebugRenderer();

		map = new TmxMapLoader().load("tutorial_map.tmx");
		map2 = new TmxMapLoader().load("tutorial_map.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
		tiledMapRenderer2 = new OrthogonalTiledMapRenderer(map2);

		//tileset = new TiledMapTileSet();
		//map.getTileSets().addTileSet(tileset);
		//map2.getTileSets().addTileSet(tileset);


        TiledUtil.parseTiledObjectLayer(world, map.getLayers().get("object_layer").getObjects());
	}

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiledMapRenderer.render();
        tiledMapRenderer2.render();
		b2dr.render(world, camera.combined.scl(PPM));
	}
	
	@Override
	public void dispose () {
		world.dispose();
		b2dr.dispose();
		map.dispose();
		tiledMapRenderer.dispose();
		map2.dispose();
		tiledMapRenderer2.dispose();
	}

	@Override
	public void resize(int width, int height){
		camera.setToOrtho(false, width / 2, height / 2);
	}

	public void update(float delta){
		world.step(1 / 30f, 6, 2);
        inputUpdate(delta);
		cameraUpdate(delta);
		tiledMapRenderer.setView(camera);
		tiledMapRenderer2.setView(camera);

	}

	public void inputUpdate(float delta){
	    int movementSpeed = 0;
	    if(Gdx.input.isTouched()){
            if(Gdx.input.getX() > width / 2){
                movementSpeed = 1;
            }
            else if(Gdx.input.getX() < width / 2){
                movementSpeed = -1;
            }
        }
        player.setLinearVelocity(movementSpeed,player.getLinearVelocity().y);
    }

	public void cameraUpdate(float delta){
        Vector3 pos = camera.position;
        pos.x = player.getPosition().x * PPM;
        pos.y = player.getPosition().y * PPM;
        camera.position.set(pos);
        camera.update();
    }


    public Body createBox(int x, int y, int w, int h, boolean isStatic){
        Body pBody;
        BodyDef def = new BodyDef();

        if(isStatic){
            def.type = BodyDef.BodyType.StaticBody;
        }
        else{
            def.type = BodyDef.BodyType.DynamicBody;
        }

        def.position.set(x / PPM, y / PPM);
        def.fixedRotation = true;

        pBody = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2 / PPM,h / 2 / PPM);
        pBody.createFixture(shape, 1.0f);
        shape.dispose();
        return pBody;
    }
}
