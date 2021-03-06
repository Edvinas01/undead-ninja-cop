package io.chark.undead_ninja_cop.engine.system.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.chark.undead_ninja_cop.core.BaseGameSystem;
import io.chark.undead_ninja_cop.core.Component;
import io.chark.undead_ninja_cop.core.Entity;
import io.chark.undead_ninja_cop.core.ResourceLoader;
import io.chark.undead_ninja_cop.core.event.EventListener;
import io.chark.undead_ninja_cop.core.util.Components;
import io.chark.undead_ninja_cop.engine.component.Transform;
import io.chark.undead_ninja_cop.engine.component.physics.Physics;
import io.chark.undead_ninja_cop.engine.component.player.DoubleJumpStrategy;
import io.chark.undead_ninja_cop.engine.component.player.JumpStrategy;
import io.chark.undead_ninja_cop.engine.component.player.Player;

import java.util.Set;

public class PlayerSystem extends BaseGameSystem {

    private static final Set<Class<? extends Component>> TYPES = Components
            .toSet(Transform.class, Physics.class, Player.class);

    private static final String LANDING_SOUND = "landing.wav";
    private static final int CAMERA_FOLLOW_SPEED = 4;
    private static final float WALK_IMPULSE = 0.01f;
    private static final float MAX_VELOCITY = 1f;

    private final Camera stationaryCamera;
    private final Camera camera;

    private final SpriteBatch spriteBatch;

    private Sound playerTouchGround;
    private BitmapFont font;

    public PlayerSystem(Camera stationaryCamera,
                        Camera camera,
                        SpriteBatch spriteBatch) {

        this.stationaryCamera = stationaryCamera;
        this.camera = camera;
        this.spriteBatch = spriteBatch;
    }

    @Override
    public void create() {
        playerTouchGround = resourceLoader.getSound(LANDING_SOUND);
        font = resourceLoader.getFont(
                ResourceLoader.FONT_KONG_TEXT,
                ResourceLoader.FONT_DEFAULT_SIZE * 2);

        // Listen when player touches ground.
        entityManager.register(new EventListener<PlayerTouchedGroundEvent>() {

            @Override
            public void onEvent(PlayerTouchedGroundEvent event) {
                Player player = event.getPlayer();

                // Reset jumping.
                player.setJumpStrategy(new DoubleJumpStrategy());
                playerTouchGround.play(CONFIG.getSettings().getSoundVolume() / 2, MathUtils.random(0.9f, 1), 0);
            }
        });
    }

    @Override
    public void updateEntities(float dt) {
        for (Entity entity : entities) {

            Transform transform = entityManager.getComponent(entity, Transform.class);
            Player player = entityManager.getComponent(entity, Player.class);
            Body body = entityManager.getComponent(entity, Physics.class)
                    .getBody();

            // Make the camera follow the player.
            if (!CONFIG.getSettings().isDebug()) {
                Vector2 transformVector = new Vector2(transform.getX(), transform.getY());
                Vector2 camPos = new Vector2(camera.position.x, camera.position.y);

                camera.position.set(camPos.lerp(transformVector, dt * CAMERA_FOLLOW_SPEED), 0);
                camera.update();
            }

            Vector2 vel = body.getLinearVelocity();

            // Cap max velocity on x.
            if (Math.abs(vel.x) > MAX_VELOCITY) {
                vel.x = Math.signum(vel.x) * MAX_VELOCITY;
                body.setLinearVelocity(vel.x, vel.y);
            }

            // Walk around.
            Vector2 pos = body.getPosition();
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                body.applyLinearImpulse(-WALK_IMPULSE, 0, pos.x, pos.y, true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                body.applyLinearImpulse(WALK_IMPULSE, 0, pos.x, pos.y, true);
            }

            // Jumping.
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

                // Resulting jump strategy after jump.
                JumpStrategy result = player.getJumpStrategy().jump(body);

                // Override old strategy using the result.
                player.setJumpStrategy(result);
            }
        }
    }

    @Override
    public void renderEntities(float dt) {
        spriteBatch.setProjectionMatrix(stationaryCamera.combined);
        spriteBatch.begin();

        // Atm only one player supported, but I guess its better to do it like this.
        for (Entity entity : entities) {
            font.draw(spriteBatch, getPlayerStatusText(entity), 32, 64);
        }
        spriteBatch.end();
    }

    @Override
    public Set<Class<? extends Component>> getComponentTypes() {
        return TYPES;
    }

    private String getPlayerStatusText(Entity entity) {
        Player player = entityManager.getComponent(entity, Player.class);
        return "Health " +
                player.getHealth() +
                "%\n" +
                "Points " +
                player.getPoints() +
                "\n";
    }
}