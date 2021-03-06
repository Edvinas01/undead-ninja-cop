package io.chark.undead_ninja_cop.engine.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.chark.undead_ninja_cop.core.GameSystem;
import io.chark.undead_ninja_cop.core.NullGameSystem;
import io.chark.undead_ninja_cop.engine.system.debug.Debugger;
import io.chark.undead_ninja_cop.engine.system.debug.DebuggerAdapter;
import io.chark.undead_ninja_cop.engine.system.physics.PhysicsSystem;
import io.chark.undead_ninja_cop.engine.system.pickup.PickupSystem;
import io.chark.undead_ninja_cop.engine.system.player.PlayerSystem;
import io.chark.undead_ninja_cop.engine.system.rendering.BackgroundRenderingSystem;
import io.chark.undead_ninja_cop.engine.system.rendering.BasicRenderingSystem;
import io.chark.undead_ninja_cop.engine.system.spawn.SpawnPointSystem;
import io.chark.undead_ninja_cop.engine.system.tiled.TiledMapSystem;

/**
 * Creates game systems.
 */
public class GameSystemFactory {

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera stationaryCamera;
    private final OrthographicCamera camera;
    private final World world;

    public GameSystemFactory(SpriteBatch spriteBatch,
                             OrthographicCamera stationaryCamera,
                             OrthographicCamera camera,
                             World world) {

        this.spriteBatch = spriteBatch;
        this.stationaryCamera = stationaryCamera;
        this.camera = camera;
        this.world = world;
    }

    /**
     * Create a game system of a specific type and add it to entity manager.
     *
     * @param type game system type.
     */
    public GameSystem create(Class<? extends GameSystem> type) {
        return BasicRenderingSystem.class.equals(type)
                ? new BasicRenderingSystem(camera, spriteBatch)
                : DebuggerAdapter.class.equals(type)
                ? new DebuggerAdapter(spriteBatch, camera, world, new Debugger()) // Adapting debugger.
                : PhysicsSystem.class.equals(type)
                ? new PhysicsSystem(world)
                : TiledMapSystem.class.equals(type)
                ? new TiledMapSystem(camera, spriteBatch, world)
                : PlayerSystem.class.equals(type)
                ? new PlayerSystem(stationaryCamera, camera, spriteBatch)
                : SpawnPointSystem.class.equals(type)
                ? new SpawnPointSystem(world)
                : BackgroundRenderingSystem.class.equals(type)
                ? new BackgroundRenderingSystem(spriteBatch)
                : PickupSystem.class.equals(type)
                ? new PickupSystem(world)
                : new NullGameSystem();
    }
}