package org.homework.repository;

import org.homework.domain.PlaySite;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface IPlayGroundRepository {
    PlaySite save(PlaySite playSite);
    Optional<PlaySite> findById(UUID id);
    void delete(UUID id);
    Collection<PlaySite> findAll();
    boolean validTicket(String ticketNumber);
}