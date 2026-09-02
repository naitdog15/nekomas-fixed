package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * The per-hit damage step a combo weapon adds, as a percentage. Read and written through
 * {@code StackData} under key {@code "combo_multiplier"}.
 * <p>
 * On 26.2 the value was baked onto the sickle as a default data component
 * ({@code 10 - material.attackDamageBonus()}), so every sickle carried one from the moment it was
 * crafted. 1.20.1 has no default-component mechanism, so the sickle supplies that starting value
 * itself ({@code ModItemSettings#sickleDefaultCombo}) and this record is only what a stack stores
 * once something writes a different one.
 * <p>
 * The tooltip text the record used to contribute through {@code TooltipProvider} — an interface with
 * no 1.20.1 counterpart — is {@link #tooltipLines()}, called from the sickle's own
 * {@code appendHoverText}.
 */
public record ComboComponent(int multiplier) {
    public static final Codec<ComboComponent> CODEC =
            Codec.INT.xmap(ComboComponent::new, ComboComponent::multiplier);

    /**
     * The two lines a combo weapon shows: the damage ramp ({@code component.nekomasfixed.combo}) and
     * the off-hand note ({@code component.nekomasfixed.duel_wield}). The ramp spells out the first
     * three steps and the tenth, which is where the combo caps.
     */
    public List<Component> tooltipLines() {
        StringBuilder ramp = new StringBuilder();
        for (int step = 1; step <= 3; step++) {
            ramp.append(step * this.multiplier).append(step < 3 ? "%, " : "%");
        }
        ramp.append(" ... ").append(10 * this.multiplier).append("%");
        return List.of(
                Component.translatable("component.nekomasfixed.combo", ramp.toString()).withStyle(ChatFormatting.GRAY),
                Component.translatable("component.nekomasfixed.duel_wield").withStyle(ChatFormatting.GRAY)
        );
    }
}
