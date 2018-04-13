package pather.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pather.game.Pather;

public class EditorScreen implements Screen {

    private Stage stage;
    private final Game game;

    private SelectBox selectBox1, selectBox2, selectBox3;
    private String module1, module2, module3;
    private final Viewport viewport;
    private Image thumbnail;
    private Array<Image> position1List, position2List, position3List;

    public EditorScreen(Game game){
        this.game = game;
        Table table = new Table();
        table.top();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("menu_art.png")))));
        table.setFillParent(true);
        //Retrieve maps from /maps/ folder
        FileHandle dir = Gdx.files.internal("maps/");
        String[] mapList = new String[dir.list().length];
        for(int i = 0; i<dir.list().length; i++){
            mapList[i] = dir.list()[i].name().substring(0, dir.list()[i].name().length()-4); //hide redundant .tmx
        }

        viewport = new FitViewport(Pather.V_WIDTH / 2, Pather.V_HEIGHT / 2, new OrthographicCamera());
        stage = new Stage(viewport, ((Pather) game).batch);
        Gdx.input.setInputProcessor(stage);

        //default libgdx skin texture
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Image playButton = new Image(new Texture(Gdx.files.internal("pather_menu_play.png")));
        playButton.setScale(0.5f);
        playButton.setPosition(viewport.getWorldWidth() / 2 - playButton.getWidth() / 4, viewport.getWorldHeight() * 0.01f);

        //Initialize drop down menus
        selectBox1 = new SelectBox(skin);
        selectBox1.setItems(mapList); //Set the map names as the menu's content
        selectBox1.setPosition(viewport.getWorldWidth() * 0.078125f, viewport.getWorldHeight() * 0.3f);
        selectBox1.setWidth(80f);

        selectBox2 = new SelectBox(skin);
        selectBox2.setItems(mapList);
        selectBox2.setWidth(80f);
        selectBox2.setPosition(viewport.getWorldWidth() / 2 - selectBox2.getWidth() / 2, viewport.getWorldHeight() * 0.3f);

        selectBox3 = new SelectBox(skin);
        selectBox3.setItems(mapList);
        selectBox3.setPosition(viewport.getWorldWidth() / 1.4f, viewport.getWorldHeight() * 0.3f);
        selectBox3.setWidth(80f);

        //Initialize arrays used in memory clearing
        position1List = new Array<Image>();
        position2List = new Array<Image>();
        position3List = new Array<Image>();


        //Set default values to selected map string to prevent crashing
        //in the case of pressing play button immediately
        module1 = (String) selectBox1.getSelected();
        module2 = (String) selectBox2.getSelected();
        module3 = (String) selectBox3.getSelected();


        //create listeners for the drop down menus
        selectBox1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                module1 = (String) selectBox1.getSelected();
                setThumbnail(module1,1); //parameters are the name of the module + position on the screen
            }
        });

        selectBox2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                module2 = (String) selectBox2.getSelected();
                setThumbnail(module2,2);
            }
        });

        selectBox3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                module3 = (String) selectBox3.getSelected();
                setThumbnail(module3,3);
            }
        });

        playButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                play();
            }
        });


        //add menus to the stage object
        stage.addActor(table);
        stage.addActor(selectBox1);
        stage.addActor(selectBox2);
        stage.addActor(selectBox3);
        stage.addActor(playButton);

    }

    public void play(){
        //initialize the game
        LoadingScreen loadingScreen = new LoadingScreen((Pather) game, new String[]{module1, module2, module3});
        game.setScreen(loadingScreen);
    }

    private void setThumbnail(String moduleName, int position){
        try{
            thumbnail = new Image(new Texture(Gdx.files.internal(moduleName + ".png"))); //retrieve the correct thumbnail image
            if(position == 1){
                if(position1List.size >= 1) {
                    for (Image image : position1List) {
                        //used to remove redundant thumbnails from memory
                        image.remove();
                    }
                    position1List.clear();
                }
                thumbnail.setPosition(viewport.getWorldWidth() * 0.078125f, viewport.getWorldHeight() * 0.6f);
                position1List.add(thumbnail);
            }
            else if(position == 2){
                if(position2List.size >= 1) {
                    for (Image image : position2List) {
                        image.remove();
                    }
                    position2List.clear();
                }
                thumbnail.setPosition(viewport.getWorldWidth() / 2 - selectBox2.getWidth() / 2, viewport.getWorldHeight() * 0.6f);
                position2List.add(thumbnail);
            }
            else if(position == 3){
                if(position3List.size >= 1) {
                    for (Image image : position3List) {
                        image.remove();
                    }
                    position3List.clear();
                }
                thumbnail.setPosition(viewport.getWorldWidth() / 1.4f, viewport.getWorldHeight() * 0.6f);
                position3List.add(thumbnail);
            }
            thumbnail.setWidth(selectBox1.getWidth());
            thumbnail.setHeight(35f);
            if(thumbnail != null){
                stage.addActor(thumbnail);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            game.setScreen(new MainMenuScreen(game));
        }
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
        thumbnail.remove();
        game.dispose();
        stage.dispose();
    }
}
