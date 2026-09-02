package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * The time a clock has been stopped at, in ticks past midnight-plus-6000 (so 0 is midnight). Read
 * and written through {@code StackData} under key {@code "stored_time"}, never a 1.21+ data
 * component.
 * <p>
 * Whether a clock HAS a recorded time is the presence of that key, not this value: 0 is a real
 * recordable reading. {@code mixin/ItemStackMixin} writes it with the generic
 * {@code StackData.write} for exactly that reason, and both readers — the glint and the tooltip —
 * ask {@code StackData.contains} first.
 * <p>
 * {@code TooltipProvider} does not exist on 1.20.1, so the line the record used to contribute is
 * {@link #tooltipLine()}, called from {@code Item#appendHoverText}.
 */
public record StoredTimeComponent(int time) {
    public static final Codec<StoredTimeComponent> CODEC =
            Codec.INT.xmap(StoredTimeComponent::new, StoredTimeComponent::time);

    /** HH:MM, zero-padded, on a 24-hour clock. */
    public String formatted() {
        int hour = time / 1000;
        int min = ((time % 1000) * 60) / 1000;
        return (hour < 10 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min;
    }

    /** The "Recorded Time: HH:MM" line, keyed {@code component.nekomasfixed.storedtime}. */
    public Component tooltipLine() {
        return Component.translatable("component.nekomasfixed.storedtime", this.formatted())
                .withStyle(ChatFormatting.GRAY);
    }
}
