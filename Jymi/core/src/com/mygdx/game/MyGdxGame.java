package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
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

import javax.naming.spi.Resolver;

import static com.mygdx.game.utils.Constants.PPM;

public class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;

	private Box2DDebugRenderer b2dr;
	private World world;
	private Body player;
	private SpriteBatch sprite;
	private Texture tex;

	private OrthogonalTiledMapRenderer tiledMapRenderer;
	private TiledMap map;

	private Controller controller;
	
	float width;
	float height;
	
	@Override
	public void create () {

		//Tarvittavien oletusresurssien siirtäminen lokaliin
		Gdx.files.internal("tilesetti.png").copyTo(Gdx.files.local("tilesetti.png"));
		Gdx.files.internal("tilesetti.tsx").copyTo(Gdx.files.local("tilesetti.tsx"));

		//Tällä hetkellä luodaan amalgamaatio kolmesta kasuaaleille suunnatuista harjoituskentistä
		MapEncoder test = new MapEncoder();
		test.decode("casual");
		test.decode("casual2");
		test.decode("casual3");
		test.encode();

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width / 2, height / 2);

		sprite = new SpriteBatch();

		world = new World(new Vector2(0, -9.8f), false);
       		player = createBox(48,48,30, 30, false);
		b2dr = new Box2DDebugRenderer();

		tex = new Texture("kappa32.png");
		map = new TmxMapLoader(new LocalFileHandleResolver()).load("temp.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);

		TiledUtil.parseTiledObjectLayer(world, map.getLayers().get("Object_layer").getObjects());
		
		controller = new Controller(width, height);

	}

	public OrthographicCamera getCamera() { return camera;}

	public SpriteBatch getBatch() { return sprite; }

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		tiledMapRenderer.render();

		sprite.begin();
		sprite.draw(tex, player.getPosition().x * PPM - tex.getWidth() / 2, player.getPosition().y * PPM - tex.getHeight() / 2);
		sprite.end();

		//b2dr.render(world, camera.combined.scl(PPM)); //HITBOX
		
		controller.draw();
	}
	
	@Override
	public void dispose () {
		world.dispose();
		b2dr.dispose();
		map.dispose();
		tiledMapRenderer.dispose();
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
		sprite.setProjectionMatrix(camera.combined);
	}

	public void inputUpdate(float delta){
		inputHandler();
    	}

	public void cameraUpdate(float delta){
		Vector3 position = camera.position;
		//Linear interpolation = a + (b - a) * lerp
		// a = current camera position
		// b = target
		// lerp = interpolation factor
		position.x = camera.position.x + (player.getPosition().x * PPM - camera.position.x) * .1f;
		position.y = camera.position.y + (player.getPosition().y * PPM - camera.position.y) * .1f;

		camera.position.set(position);
		camera.update();
	}
	
	public void inputHandler(){
		player.setLinearVelocity(new Vector2(0, player.getLinearVelocity().y));

			if(controller.isLeftPressed()){
				player.setLinearVelocity(new Vector2(-2f, player.getLinearVelocity().y));
			}
			else if(controller.isRightPressed()){
				player.setLinearVelocity(new Vector2(2f, player.getLinearVelocity().y));
			}

			if(controller.isUpPressed()){
				if(player.getLinearVelocity().y == 0f) //Voi hypätä vain, jos pystysuuntainen nopeus on 0
					player.setLinearVelocity(new Vector2(player.getLinearVelocity().x, 5f));
			}
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
