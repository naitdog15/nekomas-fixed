package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.registry.block.entity.TermitehiveBlockEntity;

import java.util.List;

/**
 * The termites a picked-up hive is carrying. Read/written through {@code StackData} under key
 * {@code "termites"}, never a 1.21+ data component. Every {@code PACKET_CODEC} on this and the other
 * three record components is deleted, not ported: 1.20.1 syncs the whole stack tag to the client for
 * free.
 * <p>
 * This one deliberately has no tooltip line. The 26.2 record carried an {@code addToTooltip}
 * producing {@code container.termitehive.termite}, but nothing ever invoked it — the dispatcher
 * listed only the animal, stored-time and combo components — and no language file has ever defined
 * that key, so the line would have rendered as the raw key had it fired. Reviving it would mean
 * inventing wording the mod never shipped; a hive's population is read off the block's
 * {@code termites} state instead, which is what the player actually sees.
 */
public record TermitesComponent(List<TermitehiveBlockEntity.TermiteData> termites) {
    public static final Codec<TermitesComponent> CODEC = TermitehiveBlockEntity.TermiteData.LIST_CODEC
            .xmap(TermitesComponent::new, TermitesComponent::termites);
    public static final TermitesComponent DEFAULT = new TermitesComponent(List.of());
}
