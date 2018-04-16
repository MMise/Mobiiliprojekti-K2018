package pather.game.Sprites;

import com.badlogic.gdx.maps.MapObject;
import pather.game.Pather;
import pather.game.Screens.PlayScreen;


/*
    This is an example based the ?-bricks in Super Mario Brothers, originally by Brent Aureli.
    In our game it acts as a danger zone that kills the player if touched
 */

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