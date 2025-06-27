package org.homework.service;

import org.homework.domain.Attraction;
import org.homework.domain.Kid;
import org.homework.domain.PlaySite;
import org.homework.repository.PlayGroundRepository;
import org.homework.request.AttractionRequest;
import org.homework.request.KidRequest;
import org.homework.request.PlayGroundRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayGroundServiceTest {

    @Mock
    PlayGroundRepository repository;
    @InjectMocks
    PlayGroundService service;

    @Test
    void createPlayGround_savesAndReturnsPlaySite() {
        PlayGroundRequest req = new PlayGroundRequest("Fun Park",
                List.of(new AttractionRequest(Attraction.Type.SLIDE, 5)));
        ArgumentCaptor<PlaySite> captor = ArgumentCaptor.forClass(PlaySite.class);

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlaySite result = service.createPlayGround(req);

        verify(repository).save(captor.capture());
        assertEquals("Fun Park", captor.getValue().getName());
        assertNotNull(result);
    }

    @Test
    void updatePlayGround_updatesNameAndAttractionsAndResetsKidsAndQueue() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlaySite old = service.createPlayGround(new PlayGroundRequest("Old Name", List.of(new AttractionRequest(Attraction.Type.SLIDE, 1))));
        when(repository.findById(old.getId())).thenReturn(Optional.of(old));
        service.addKid(old.getId(), new KidRequest("Alice", 7, "T1", true));
        service.addKid(old.getId(), new KidRequest("Tom", 8, "T2", true));

        PlayGroundRequest req = new PlayGroundRequest(
                "New Name",
                List.of(new AttractionRequest(Attraction.Type.CAROUSEL, 3))
        );

        PlaySite updated = service.updatePlayGround(old.getId(), req);

        assertEquals("New Name", updated.getName());
        assertEquals(1, updated.getAttractions().size());
        assertEquals(Attraction.Type.CAROUSEL, updated.getAttractions().get(0).type());
        assertEquals(3, updated.getAttractions().get(0).capacity());
        assertTrue(updated.getKids().isEmpty());
        assertTrue(updated.getQueue().isEmpty());
    }

    @Test
    void updatePlayGround_throwsIfNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(repository.findById(unknownId)).thenReturn(Optional.empty());
        PlayGroundRequest req = new PlayGroundRequest("X", List.of(new AttractionRequest(Attraction.Type.SLIDE, 1)));
        assertThrows(NoSuchElementException.class, () -> service.updatePlayGround(unknownId, req));
    }

    @Test
    void updatePlayGround_savesUpdatedPlaySite() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlaySite old = service.createPlayGround(new PlayGroundRequest("Old Name", List.of(new AttractionRequest(Attraction.Type.SLIDE, 1))));
        when(repository.findById(old.getId())).thenReturn(Optional.of(old));

        PlayGroundRequest req = new PlayGroundRequest(
                "New Name",
                List.of(new AttractionRequest(Attraction.Type.BALL_PIT, 2))
        );

        service.updatePlayGround(old.getId(), req);

        ArgumentCaptor<PlaySite> captor = ArgumentCaptor.forClass(PlaySite.class);
        verify(repository, times(2)).save(captor.capture());
        assertEquals("New Name", captor.getValue().getName());
        assertEquals(Attraction.Type.BALL_PIT, captor.getValue().getAttractions().get(0).type());
    }

    @Test
    void addKid_addsKidIfCapacityAllows() {
        PlaySite site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 1)));
        UUID id = site.getId();
        when(repository.findById(id)).thenReturn(Optional.of(site));
        when(repository.validTicket(any())).thenReturn(true);

        KidRequest kidReq = new KidRequest("Alice", 7, "T1", false);

        boolean added = service.addKid(id, kidReq);

        assertTrue(added);
        assertEquals(100, site.getUtilization());
    }

    @Test
    void addKid_doesNotAddWhenAtFullCapacity() {
        PlaySite site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 1)));
        UUID id = site.getId();
        site.addKid(new Kid("Bob", 8, "T1"), false);
        when(repository.findById(id)).thenReturn(Optional.of(site));
        when(repository.validTicket(any())).thenReturn(true);

        KidRequest kidReq = new KidRequest("Alice", 7, "T2", false);

        boolean added = service.addKid(id, kidReq);

        assertFalse(added);
        assertEquals(100, site.getUtilization());
    }

    @Test
    void addKid_addsToQueueWhenAcceptQueueIsTrue() {
        PlaySite site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 1)));
        UUID id = site.getId();
        site.addKid(new Kid("Bob", 8, "T1"), false);
        when(repository.findById(id)).thenReturn(Optional.of(site));
        when(repository.validTicket(any())).thenReturn(true);

        KidRequest kidReq = new KidRequest("Alice", 7, "T2", true);

        boolean added = service.addKid(id, kidReq);

        assertFalse(added);
        assertEquals(100, site.getUtilization());
        assertEquals(1, site.getQueue().size());

        service.removeKid(id,"T1");

        assertEquals(1, site.getCapacity());
        assertEquals(0, site.getQueue().size());
    }

    @Test
    void addKid_doesNotAddWhenTicketInvalid() {
        PlaySite site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 2)));
        UUID id = site.getId();
        site.addKid(new Kid("Bob", 8, "T1"), false);
        when(repository.validTicket("T1")).thenReturn(false);

        KidRequest kidReq = new KidRequest("Alice", 7, "T1", false);

        boolean added = service.addKid(id, kidReq);
        assertFalse(added);
    }

    @Test
    void removeKid_doesNothingIfKidNotPresent() {
        PlaySite site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 1)));
        UUID id = site.getId();
        when(repository.findById(id)).thenReturn(Optional.of(site));

        service.removeKid(id, "NonExistentTicket");

        assertEquals(0, site.getUtilization());
    }

    @Test
    void getUtilization_returnsCorrectValue() {
        PlaySite site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 2)));
        UUID id = site.getId();
        when(repository.findById(id)).thenReturn(Optional.of(site));

        site.addKid(new Kid("Bob", 8, "T2"), false);

        int utilization = service.getUtilization(id);

        assertEquals(50, utilization);
    }

    @Test
    void getTotalVisitorsToday_sumsAllPlaySites() {
        PlaySite s1 = new PlaySite("A", List.of(new Attraction(Attraction.Type.SLIDE, 1)));
        PlaySite s2 = new PlaySite("B", List.of(new Attraction(Attraction.Type.SLIDE, 1)));
        s1.addKid(new Kid("Bob", 8, "T1"), false);
        s2.addKid(new Kid("Alice", 7, "T2"), false);

        when(repository.findAll()).thenReturn(List.of(s1, s2));

        int total = service.getTotalVisitorsToday();

        assertEquals(2, total);
    }

    @Test
    void getUtilization_returnsZeroWhenNoKids() {
        PlaySite site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 2)));
        UUID id = site.getId();
        when(repository.findById(id)).thenReturn(Optional.of(site));

        int utilization = service.getUtilization(id);

        assertEquals(0, utilization);
    }
}
