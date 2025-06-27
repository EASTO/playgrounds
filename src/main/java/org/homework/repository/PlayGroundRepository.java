package org.homework.repository;

import org.homework.domain.PlaySite;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PlayGroundRepository {

    private final Map<UUID, PlaySite> playGroundMap = new HashMap<>();

    public PlaySite save(PlaySite playSite) {
        playGroundMap.put(playSite.getId(), playSite);
        return playSite;
    }

    public Optional<PlaySite> findById(UUID id) {
        return Optional.ofNullable(playGroundMap.get(id));
    }

    public void delete(UUID id) {
        playGroundMap.remove(id);
    }

    public Collection<PlaySite> findAll() {
        return playGroundMap.values();
    }

    public boolean validTicket(String ticketNumber) {
        return playGroundMap.values().stream()
                .flatMap(ps -> ps.getKids().stream())
                .noneMatch(kid -> kid.ticketNumber().equals(ticketNumber));
    }
}