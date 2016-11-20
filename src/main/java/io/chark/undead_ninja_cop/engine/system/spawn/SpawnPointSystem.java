package io.chark.undead_ninja_cop.engine.system.spawn;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.chark.undead_ninja_cop.core.BaseGameSystem;
import io.chark.undead_ninja_cop.core.Component;
import io.chark.undead_ninja_cop.core.Entity;
import io.chark.undead_ninja_cop.core.util.Components;
import io.chark.undead_ninja_cop.engine.component.player.Player;
import io.chark.undead_ninja_cop.engine.component.SpawnPoint;
import io.chark.undead_ninja_cop.engine.component.Transform;
import io.chark.undead_ninja_cop.engine.component.physics.FixtureBuilder;
import io.chark.undead_ninja_cop.engine.component.physics.PhysicsBuilder;

import java.util.Arrays;
import java.util.Set;

public class SpawnPointSystem extends BaseGameSystem {

    private static final Set<Class<? extends Component>> TYPES = Components
            .toSet(Transform.class, SpawnPoint.class);

    private static final float HEIGHT = 15;
    private static final float WIDTH = 5;

    private final World world;

    // todo use messaging for this stuff.
    private boolean spawned = false;

    public SpawnPointSystem(World world) {
        this.world = world;
    }

    @Override
    public void updateEntities(float dt) {
        if (!spawned) {
            for (Entity entity : entities) {
                SpawnPoint point = entityManager.getComponent(entity, SpawnPoint.class);
                if (SpawnPoint.Type.PLAYER.equals(point.getType())) {
                    Transform spawn = entityManager.getComponent(entity, Transform.class);

                    Player player = new Player();

                    entityManager.createEntity(Arrays.asList(
                            new Transform(0, 0),
                            player,
                            PhysicsBuilder
                                    .usingWorld(world)
                                    .dynamic()
                                    .bullet()
                                    .fixedRotation()
                                    .position(spawn.getX(), spawn.getY())
                                    .addFixture(FixtureBuilder.builder()
                                            .dimensions(WIDTH, HEIGHT)
                                            .density(1)
                                            .build(Shape.Type.Polygon))
                                    .addFixture(player, FixtureBuilder.builder()
                                            .radius(WIDTH)
                                            .position(0, -HEIGHT)
                                            .build(Shape.Type.Circle))
                                    .build()));

                    spawned = true;
                }
            }
        }
    }

    @Override
    public Set<Class<? extends Component>> getComponentTypes() {
        return TYPES;
    }
}