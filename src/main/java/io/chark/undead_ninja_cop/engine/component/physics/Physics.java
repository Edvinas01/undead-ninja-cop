package io.chark.undead_ninja_cop.engine.component.physics;

import com.badlogic.gdx.physics.box2d.Body;
import io.chark.undead_ninja_cop.core.Component;

public class Physics implements Component {

    private final Body body;

    Physics(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }
}