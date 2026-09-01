package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;

/**
 * STORED_TIME, ported unchanged as a value + {@link #CODEC} — read/written
 * through {@code StackData} under key {@code "stored_time"}, never a 1.21+ data component.
 * <p>
 * {@code TooltipProvider}/{@code DataComponentGetter}/{@code Item.TooltipContext} do not exist on
 * 1.20.1 — data components are a REDESIGN, not a port, and must be designed once, centrally. The
 * tooltip text this record used to contribute via {@code addToTooltip} is re-homed by
 * whichever package owns the consuming item's {@code appendHoverText} — the vanilla clock.
 */
public record StoredTimeComponent(int time) {
    public static final Codec<StoredTimeComponent> CODEC =
            Codec.INT.xmap(StoredTimeComponent::new, StoredTimeComponent::time);

    /** Mirrors the deleted {@code addToTooltip}'s HH:MM formatting, for the mixin/item to call. */
    public String formatted() {
        int hour = time / 1000;
        int min = ((time % 1000) * 60) / 1000;
        return (hour < 10 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min;
    }
}
