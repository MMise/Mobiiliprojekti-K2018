package pather.game.Sprites;

import com.badlogic.gdx.maps.MapObject;

import pather.game.Pather;
import pather.game.Scenes.Hud;
import pather.game.Screens.PlayScreen;

//Example on the breakable brick object in Super Mario Bros. Originally by Brent Aureli

public class Brick extends InteractiveTileObject {

    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Pather.WIN_BIT);
    }

    @Override
    public void onHeadHit(Player player) {

    }
}