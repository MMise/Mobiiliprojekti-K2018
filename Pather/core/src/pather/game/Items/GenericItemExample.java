package pather.game.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import pather.game.Pather;
import pather.game.Screens.PlayScreen;
import pather.game.Sprites.Player;

/*  This is in example based on the Mushroom in Super Mario Brothers by Brent Aureli.
    This class is not used in the final product because our pickable items don't need to move
    The class PickableTileObject implements the power up logic
    */

public class GenericItemExample extends Item {

    public GenericItemExample(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        // find the corresponding sprite in the packed sprite file
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody; //Dynamic Body means this object is affected by gravity
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Pather.PPM);
        fdef.filter.categoryBits = Pather.ITEM_BIT; //We define this object as an item for collisions
        fdef.filter.maskBits =  Pather.PLAYER_BIT |
                                Pather.OBJECT_BIT |
                                Pather.GROUND_BIT |
                                Pather.WIN_BIT |
                                Pather.DANGERZONE_BIT;
        //Above object are what the item can collide with

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Player player) {
        destroy();
        System.out.println("Power up was picked up"); //Change these to reflect the items effects
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
