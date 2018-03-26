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

public class LethalTile extends InteractiveTileObject {


    public LethalTile(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Pather.DANGERZONE_BIT);
    }

    @Override
    public void onHit(Player player) {

    }
}