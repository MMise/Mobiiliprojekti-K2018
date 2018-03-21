package pather.game.Sprites;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

import pather.game.Pather;
import pather.game.Screens.PlayScreen;



public class PickableTileObject extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int tileIndex = 83; //Tiled tileset ID + 1

    public PickableTileObject(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("fantasy-tileset"); //Change tileset name
        fixture.setUserData(this);
        fixture.setSensor(true);
        setCategoryFilter(MainApp.TRIGGER_BIT); //Add this short to main class
    }

    @Override
    public void onHit(Player player) {
        getCell().setTile(tileSet.getTile(tileIndex));
        //doStuff
    }
}
