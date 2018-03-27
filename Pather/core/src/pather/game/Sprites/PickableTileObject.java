package pather.game.Sprites;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

import pather.game.Pather;
import pather.game.Screens.PlayScreen;



public class PickableTileObject extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int tileIndex = 49; //Tiled tileset ID + 1
    private boolean used = false;

    public PickableTileObject(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("sheet1"); //Change tileset name
        fixture.setUserData(this);
        fixture.setSensor(true);
        setCategoryFilter(Pather.ITEM_BIT); //Add this short to main class
    }

    @Override
    public void onHit(Player player) {
        getCell().setTile(null);
        player.useItem();
    }
}
