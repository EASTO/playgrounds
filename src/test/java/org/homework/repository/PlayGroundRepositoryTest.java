package org.homework.repository;

import org.homework.domain.Attraction;
import org.homework.domain.Kid;
import org.homework.domain.PlaySite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PlayGroundRepositoryTest {

    PlayGroundRepository repository;
    PlaySite site;

    @BeforeEach
    void setUp() {
        repository = new PlayGroundRepository();
        site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 2)));
    }

    @Test
    void save_and_findById() {
        repository.save(site);
        Optional<PlaySite> found = repository.findById(site.getId());
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());
    }

    @Test
    void delete_removesPlaySite() {
        repository.save(site);
        repository.delete(site.getId());
        assertTrue(repository.findById(site.getId()).isEmpty());
    }

    @Test
    void findAll_returnsAllSites() {
        repository.save(site);
        PlaySite site2 = new PlaySite("Other", List.of(new Attraction(Attraction.Type.SLIDE, 1)));
        repository.save(site2);
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void validTicket_returnsTrueIfTicketUnused() {
        repository.save(site);
        assertTrue(repository.validTicket("T1"));
    }

    @Test
    void validTicket_returnsFalseIfTicketUsed() {
        site.addKid(new Kid("Alice", 7, "T1"), false);
        repository.save(site);
        assertFalse(repository.validTicket("T1"));
    }
}
