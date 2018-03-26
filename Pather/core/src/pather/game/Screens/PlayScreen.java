package pather.game.Screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.concurrent.LinkedBlockingQueue;

import pather.game.Controller;
import pather.game.Items.GenericItemExample;
import pather.game.Items.Item;
import pather.game.Items.ItemDef;
import pather.game.Pather;
import pather.game.Scenes.Hud;
import pather.game.Sprites.Enemy;
import pather.game.Sprites.Player;
import pather.game.Tools.B2WorldCreator;
import pather.game.Tools.MapEncoder;
import pather.game.Tools.WorldContactListener;


//This is our main play screen where all the game functionality happens

public class PlayScreen implements Screen {

    //TODO: pressing the back button should take you back to the menu

    private Pather game;
    private TextureAtlas atlas; //this contains all of our game sprites

    //playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2D variables
    private World world;
    private Box2DDebugRenderer b2dr; //This isn't needed in the final version of our game
    private B2WorldCreator creator;
    //sprites
    private Player player;
    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    private float w, h;
    private float gravity = -20;

    private Controller controller;

    public PlayScreen(Pather game) {
        //Tilesetit on oltava saatavilla lokaalissa
        Gdx.files.internal("tileset_gutter.png").copyTo(Gdx.files.local("tileset_gutter.png"));
        Gdx.files.internal("sci-fi-platformer-tiles-32x32-extension.png").copyTo(Gdx.files.local("sci-fi-platformer-tiles-32x32-extension.png"));
        Gdx.files.internal("steampunk_tiles.png").copyTo(Gdx.files.local("steampunk_tiles.png"));
        Gdx.files.internal("sheet1.png").copyTo(Gdx.files.local("sheet1.png"));
        Gdx.files.internal("winzone_tileset.png").copyTo(Gdx.files.local("winzone_tileset.png"));

        atlas = new TextureAtlas("packed_gfx.pack"); //Pack all of our sprites into a single file
        this.game = game;

        w = (float) Gdx.graphics.getWidth();
        h = (float) Gdx.graphics.getHeight();

        //create cam to follow player through the world
        gamecam = new OrthographicCamera();

        //fitviewport maintains aspect ratio despite screen size
        gamePort = new StretchViewport(Pather.V_WIDTH / Pather.PPM, Pather.V_HEIGHT / Pather.PPM, gamecam);

        //create hud for score, timer etc
        hud = new Hud(game.batch);

        //load the map and setup map renderer.
        maploader = new TmxMapLoader(new LocalFileHandleResolver()); //Levels are generated in local memory
        //maploader = new TmxMapLoader();
        if (!Gdx.files.local("temp.tmx").exists()) {
            MapEncoder encoder = new MapEncoder();

            //TODO modules to be loaded
            encoder.decode("module1");
            encoder.decode("module2");
            encoder.decode("module3");

            encoder.encode();
        }
        map = maploader.load("temp.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Pather.PPM);

        //Box2D variables
        world = new World(new Vector2(0, gravity), true); //This creates a world where gravity works like on Earth
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);
        player = new Player(this);

        //set gamecam to correct position in the beginning of the game
        gamecam.position.set(player.b2body.getPosition().x, gamePort.getWorldHeight() / 2, 0);

        world.setContactListener(new WorldContactListener());

        /*TODO: Uncomment this to bless the rains

        music = Pather.manager.get("audio/music/africa.wav", Music.class);
        music.setLooping(true);
        music.play();

        */

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

        controller = new Controller(w, h); //Create touchscreen controls
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        //Set items to spawn inside specific locations. Locations must be defined in Tiled
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == GenericItemExample.class){
                items.add(new GenericItemExample(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {

        //TODO: Jumping is now allowed when vertical velocity is 0.
        //This can lead to exploits, implement a method to check for ground in the future
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            game.setScreen(new MainMenuScreen(game));
            Gdx.app.log("PlayScreen", "BACK PRESSED");
        }

        if(player.currentState != Player.State.DEAD){
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().y == 0 ||
                    controller.isUpPressed() && player.b2body.getLinearVelocity().y == 0 ) {
                player.b2body.setLinearVelocity(new Vector2(player.b2body.getLinearVelocity().x, 12f));
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                    controller.isRightPressed() ) {
                player.b2body.applyLinearImpulse(new Vector2(1f, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                    controller.isLeftPressed() ) {
                player.b2body.applyLinearImpulse(new Vector2(-1f, 0), player.b2body.getWorldCenter(), true);
            }
            float speed = player.b2body.getLinearVelocity().x;
            player.b2body.setLinearVelocity(Math.min(Math.abs(speed), 6f)*(speed == 0f ? 1 : Math.abs(speed)/speed), player.b2body.getLinearVelocity().y);
        }
    }

    //This adds slight linear interpolation to camera movement
    public void cameraUpdate(float dt) {
        Vector3 position = gamecam.position;
        position.x = gamecam.position.x + (player.b2body.getPosition().x - gamecam.position.x) * .2f;
        position.y = gamecam.position.y + (player.b2body.getPosition().y - gamecam.position.y) * .2f;
        gamecam.position.set(position);
        gamecam.update();
    }

    public void update(float dt){
        //Handle user input first
        handleInput(dt);
        handleSpawningItems();
        //takes 1 step in the physics simulation
        world.step(1/60f, 6, 2);

        player.update(dt);
        //Set enemies active only if we come near enough
        for(Enemy enemy : creator.getEnemies()){
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 576 / Pather.PPM){
                enemy.b2body.setActive(true);
            }
        }

        for(Item item : items){
            item.update(dt);
        }
        hud.update(dt);

        //update camera location
        if(player.currentState != Player.State.DEAD){
            cameraUpdate(dt);
        }

        //tell our renderer to draw only what can be seen on the screen
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        //separate update logic from render
        update(delta);

        //clear the game screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render the game map
        renderer.render();

        //render our debug lines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getEnemies()){
            enemy.draw(game.batch);
        }
        for(Item item : items){
            item.draw(game.batch);
        }
        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        controller.draw();
        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    //The game over screen appears after three seconds of dying
    public boolean gameOver(){
        if(player.currentState == Player.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    public void setGravity(float value){
        gravity = value;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
