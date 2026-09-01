package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;

/**
 * COMBO_MULTIPLIER, ported unchanged as a value + {@link #CODEC} — read/written
 * through {@code StackData} under key {@code "combo_multiplier"}. See {@link StoredTimeComponent}
 * for why {@code TooltipProvider} is not implemented here; the sickle tooltip text this record
 * used to contribute is re-homed onto the sickle items' own {@code appendHoverText}.
 */
public record ComboComponent(int multiplier) {
    public static final Codec<ComboComponent> CODEC =
            Codec.INT.xmap(ComboComponent::new, ComboComponent::multiplier);
}
