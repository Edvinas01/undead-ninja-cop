package io.chark.undead_ninja_cop.core;

import java.util.Collection;

/**
 * Creates and removes entities, manages entity systems.
 */
public interface EntityManager {

    /**
     * Create an entity with given components.
     *
     * @param components components used in entity creation.
     * @return created entity.
     */
    Entity createEntity(Collection<Component> components);

    /**
     * Remove entity.
     *
     * @param entity entity to remove.
     */
    void removeEntity(Entity entity);

    /**
     * Remove all entities.
     */
    void removeEntities();

    /**
     * Get all components for an entity.
     *
     * @param entity entity to get the components for.
     * @return collection of components.
     */
    Collection<Component> getComponents(Entity entity);

    /**
     * Get a single component for entity.
     *
     * @param entity entity to ge the component for.
     * @param type   component type.
     * @return component.
     */
    <T extends Component> T getComponent(Entity entity, Class<T> type);

    /**
     * Add a new entity system to the entity manager.
     *
     * @param system system to add.
     */
    void addSystem(EntitySystem system);

    /**
     * Update all entity systems.
     */
    void updateSystems();
}