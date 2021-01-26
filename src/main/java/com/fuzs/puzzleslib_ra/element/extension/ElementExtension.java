package com.fuzs.puzzleslib_ra.element.extension;

import com.fuzs.puzzleslib_ra.element.EventListener;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * abstract template for sided elements complementing a common element
 */
public abstract class ElementExtension<T extends ExtensibleElement<?>> extends EventListener {

    /**
     * common element this belongs to
     */
    private final T parent;

    /**
     * create new with parent
     * @param parent parent
     */
    public ElementExtension(T parent) {

        this.parent = parent;
    }

    /**
     * @return common parent for this
     */
    public final T getParent() {

        return this.parent;
    }

    @Override
    public final List<EventStorage<? extends Event>> getEvents() {

        return this.getParent().getEvents();
    }

}
