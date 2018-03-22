package pather.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.concurrent.LinkedBlockingQueue;
import pather.game.Items.GenericItemExample;
import pather.game.Items.Item;
import pather.game.Items.ItemDef;
import pather.game.Pather;
import pather.game.Scenes.Hud;
import pather.game.Sprites.Enemy;
import pather.game.Sprites.Player;
import pather.game.Tools.B2WorldCreator;
import pather.game.Tools.WorldContactListener;

//This is our main play screen where all the game functionality happens

public class PlayScreen implements Screen {

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

    //private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    float w, h;

    public PlayScreen(Pather game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack"); //Pack all of our sprites into a single file
        this.game = game;

        w = (float) Gdx.graphics.getWidth();
        h = (float) Gdx.graphics.getHeight();
        //create cam to follow player through the world
        gamecam = new OrthographicCamera();
        //gamecam.setToOrtho(false, w / Pather.SCALE, h / Pather.SCALE);

        //fitviewport maintains aspect ratio despite screen size
        gamePort = new FitViewport(Pather.V_WIDTH / Pather.PPM, Pather.V_HEIGHT / Pather.PPM, gamecam);

        //create hud for score, timer etc
        hud = new Hud(game.batch);

        //load the map and setup map renderer.
        //TODO: Jylkkä plz work your level building magic here
        maploader = new TmxMapLoader();
        map = maploader.load("module1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Pather.PPM);

        //Box2D variables
        world = new World(new Vector2(0, -10), true); //This creates a world where gravity works like on Earth
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);
        player = new Player(this);

        //set gamecam to correct position in the beginning of the game
        gamecam.position.set(player.b2body.getPosition().x, gamePort.getWorldHeight() / 2, 0);

        world.setContactListener(new WorldContactListener());

        //music = Pather.manager.get("audio/music/mario_music.ogg", Music.class);
        //music.setLooping(true);
        //music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
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

        //TODO: Change this to reflect our touch screen controls in future

         if(player.currentState != Player.State.DEAD){
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                    //This makes jumping work like in the game Flappy Bird
                    player.b2body.setLinearVelocity(new Vector2(player.b2body.getLinearVelocity().x, 10f));
                }
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 7) {
                    player.b2body.setLinearVelocity(new Vector2(7f, player.b2body.getLinearVelocity().y));
                }
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -7) {
                    player.b2body.setLinearVelocity(new Vector2(-7f, player.b2body.getLinearVelocity().y));
                }
            }
        }

    //This adds slight linear interpolation to camera movement
    public void cameraUpdate(float dt){
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
