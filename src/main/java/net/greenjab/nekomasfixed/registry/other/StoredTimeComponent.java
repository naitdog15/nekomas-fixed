package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;

/**
 * STORED_TIME, ported unchanged as a value + {@link #CODEC} — read/written
 * through {@code StackData} under key {@code "stored_time"}, never a 1.21+ data component.
 * <p>
 * {@code TooltipProvider}/{@code DataComponentGetter}/{@code Item.TooltipContext} do not exist on
 * 1.20.1, so a record cannot contribute tooltip text of its own here. The line this one used to
 * add via {@code addToTooltip} belongs on the consuming item's {@code appendHoverText} — the
 * vanilla clock.
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
