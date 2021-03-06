
package pather.game.Tools;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import pather.game.Items.Item;
import pather.game.Pather;
import pather.game.Sprites.Enemy;
import pather.game.Sprites.Hopper;
import pather.game.Sprites.InteractiveTileObject;
import pather.game.Sprites.PickableTileObject;
import pather.game.Sprites.Player;

//This is the class that handles our collisions, built upon an example by Brent Aureli

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        //We define the two objects that just collided
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // We use bitmasking to determine objects that just collided
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        /*
            Change the cases to reflect collidable objects in our game.
            Each case must have have an if-else construct to reflect situations
            where the desired object functionality resides either in fixA or fixB.
            This is because we have no way of knowing which fixture is our desired
            object.
            Remember to cast the object, which the fixA.getUserData() method returns,
            to our desired object class.
         */
        switch(cDef){
            case Pather.PLAYER_BIT | Pather.WIN_BIT:
                if(fixA.getFilterData().categoryBits == Pather.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).win();
                }else{
                    ((Player) fixB.getUserData()).win();
                }
                break;
            case Pather.PLAYER_BIT | Pather.DANGERZONE_BIT:
                if(fixA.getFilterData().categoryBits == Pather.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).kill();
                }else{
                    ((Player) fixB.getUserData()).kill();
                }
                break;

            case Pather.ENEMY_BIT | Pather.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Pather.ENEMY_BIT){
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                }else{
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            case Pather.PLAYER_BIT | Pather.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == Pather.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).kill();
                }else {
                    ((Player) fixB.getUserData()).kill();
                }
                break;
            case Pather.ENEMY_BIT | Pather.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).onEnemyHit((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).onEnemyHit((Enemy)fixA.getUserData());
                break;
            case Pather.PLAYER_HEAD_BIT | Pather.OBJECT_BIT:
            case Pather.PLAYER_HEAD_BIT | Pather.GROUND_BIT:
                if(Pather.toggleSound)
                    Pather.manager.get("audio/sounds/bump.wav", Sound.class).play();

                break;
            /*
            case Pather.ITEM_BIT | Pather.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Pather.ITEM_BIT){
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                }else{
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            */
            case Pather.ITEM_BIT | Pather.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == Pather.ITEM_BIT){
                    ((PickableTileObject)fixA.getUserData()).onHit((Player) fixB.getUserData());
                }else{
                    ((PickableTileObject)fixB.getUserData()).onHit((Player) fixA.getUserData());
                }
                break;
            case Pather.ENEMY_BIT | Pather.DANGERZONE_BIT:
                if(fixA.getUserData() instanceof Hopper || fixB.getUserData() instanceof Hopper){
                    if(fixA.getFilterData().categoryBits == Pather.ENEMY_BIT){
                        ((Hopper)fixA.getUserData()).setToDestroy();
                    }else{
                        ((Hopper)fixB.getUserData()).setToDestroy();
                    }
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
