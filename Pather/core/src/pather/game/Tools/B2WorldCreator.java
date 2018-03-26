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
import pather.game.Sprites.Brick;
import pather.game.Sprites.Coin;
import pather.game.Sprites.Enemy;
import pather.game.Sprites.Goomba;
import pather.game.Sprites.GroundTile;
import pather.game.Sprites.NotGoomba;
import pather.game.Sprites.PickableTileObject;
import pather.game.Sprites.Turtle;

import static pather.game.Pather.PPM;

//This is a tool to create our levels from Tiled files, built open Brent Aureli's example
//TODO: Integrate Jylkkä's dynamic world builder

public class B2WorldCreator {

    private Array<NotGoomba> goombas;
    private Array<Turtle> turtles;

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
        //create pipe bodies/fixtures
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
        //create coin bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }

        //create all goombas
        goombas = new Array<NotGoomba>();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new NotGoomba(screen, rect.getX() / PPM, rect.getY() / Pather.PPM));
        }
    }

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        return enemies;
    }
}
