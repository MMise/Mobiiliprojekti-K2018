package pather.game.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import pather.game.Pather;
import pather.game.Screens.PlayScreen;
import pather.game.Sprites.Player;

//This is in example based on the Mushroom in Super Mario Brothers by Brent Aureli.

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
                                Pather.BRICK_BIT |
                                Pather.COIN_BIT;
        //Above object are what the item can collide with

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Player player) {
        destroy();
        player.grow(); //Change these to reflect the items effects
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
