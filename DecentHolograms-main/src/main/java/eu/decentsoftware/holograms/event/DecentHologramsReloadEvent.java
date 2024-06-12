package eu.decentsoftware.holograms.event;

import org.bukkit.event.HandlerList;

/**
 * This event is called after the DecentHolograms plugin is reloaded.
 *
 * @author d0by
 * @since 2.7.8
 */
public class DecentHologramsReloadEvent extends DecentHologramsEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public DecentHologramsReloadEvent() {
        super(true);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static boolean isRegistered() {
        return HANDLERS.getRegisteredListeners().length > 0;
    }

}
