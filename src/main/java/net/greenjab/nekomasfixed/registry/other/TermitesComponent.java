package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.registry.block.entity.TermitehiveBlockEntity;

import java.util.List;

// PACKET_CODEC dropped - 1.20.1 syncs the whole stack tag to the client for free, no need for it.
// no tooltip line on purpose: the original line was never wired up and no lang file ever defined its key
public record TermitesComponent(List<TermitehiveBlockEntity.TermiteData> termites) {
    public static final Codec<TermitesComponent> CODEC = TermitehiveBlockEntity.TermiteData.LIST_CODEC
            .xmap(TermitesComponent::new, TermitesComponent::termites);
    public static final TermitesComponent DEFAULT = new TermitesComponent(List.of());
}
