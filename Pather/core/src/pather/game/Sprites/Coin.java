package pather.game.Sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;

import pather.game.Items.GenericItemExample;
import pather.game.Items.ItemDef;
import pather.game.Pather;
import pather.game.Scenes.Hud;
import pather.game.Screens.PlayScreen;


//This is an example on the ?-bricks in Super Mario Brothers, originally by Brent Aureli

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28; //The index of the tileset piece for ?-brick that has been hit

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        //Change this to reflect our tileset
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(Pather.DANGERZONE_BIT);
    }

    @Override
    public void onHit(Player player) {
        if(getCell().getTile().getId() == BLANK_COIN){
            //This happens if the player hits a ?-brick again
            //Pather.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }else{
            //If our ?-brick has the property "mushroom" defined in tiled, it will spawn a mushroom item
            if(object.getProperties().containsKey("mushroom")){
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / Pather.PPM),
                        GenericItemExample.class));
                //Pather.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }else{
                //Pather.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            Hud.addScore(100);
        }
    }
}
