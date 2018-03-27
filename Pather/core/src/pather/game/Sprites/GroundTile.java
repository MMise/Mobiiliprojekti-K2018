package pather.game.Sprites;

import com.badlogic.gdx.maps.MapObject;

import pather.game.Pather;
import pather.game.Screens.PlayScreen;

/**
 * Created by OMISTAJ on 22.3.2018.
 */

public class GroundTile extends InteractiveTileObject {

    /*
        Our ground tile is an extension of an interactive tile object
        If the ground tile has a "win"-property in Tiled,
        collision with this specific tile results in the player clearing the level
    */

    public GroundTile(PlayScreen screen, MapObject object) {
        super(screen, object);

        fixture.setUserData(this);
        if(object.getProperties().containsKey("win")){
            setCategoryFilter(Pather.WIN_BIT);
        }else{
            setCategoryFilter(Pather.GROUND_BIT);
        }
    }

    @Override
    public void onHit(Player player) {

    }
}
