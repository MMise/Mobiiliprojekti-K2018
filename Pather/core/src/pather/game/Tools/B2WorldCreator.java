package pather.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import pather.game.Pather;
import pather.game.Screens.PlayScreen;
import pather.game.Sprites.Enemy;
import pather.game.Sprites.GroundTile;
import pather.game.Sprites.Hopper;
import pather.game.Sprites.LethalTile;
import pather.game.Sprites.PickableTileObject;

import static pather.game.Pather.PPM;

//This is a tool to create our levels from Tiled files, built open Brent Aureli's example

public class B2WorldCreator {

    private Array<Hopper> hoppers;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        //the .get(x) -method finds the object layer in index x in the tiled filed.
        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            new GroundTile(screen, object);
        }
        //create object bodies/fixtures. Enemies colliding with these are supposed to reverse their x-velocity
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / PPM, (rect.getY() + rect.getHeight() / 2) / PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / PPM, rect.getHeight() / 2 / PPM);
            fdef.shape = shape;
			fdef.friction = 5f;
            fdef.filter.categoryBits = Pather.OBJECT_BIT;
            body.createFixture(fdef);
        }
        //create powerups
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new PickableTileObject(screen, object);
        }
        //create danger zone objects
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new LethalTile(screen, object);
        }

        //create all enemies
        hoppers = new Array<Hopper>();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            hoppers.add(new Hopper(screen, rect.getX() / PPM, rect.getY() / Pather.PPM));
        }
    }

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(hoppers);
        return enemies;
    }
}
