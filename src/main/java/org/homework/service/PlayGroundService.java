package org.homework.service;

import org.homework.domain.Attraction;
import org.homework.domain.Kid;
import org.homework.domain.PlaySite;
import org.homework.repository.PlayGroundRepository;
import org.homework.request.KidRequest;
import org.homework.request.PlayGroundRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayGroundService {

    private final PlayGroundRepository playGroundRepository;

    public PlayGroundService(PlayGroundRepository playGroundRepository) {
        this.playGroundRepository = playGroundRepository;
    }

    public PlaySite createPlayGround(PlayGroundRequest playGroundRequest) {
        List<Attraction> attractions = new ArrayList<>();
        playGroundRequest.attractions().forEach(a -> attractions.add(new Attraction(a.type(), a.capacity())));
        PlaySite ps = new PlaySite(playGroundRequest.name(), attractions);
        return playGroundRepository.save(ps);
    }

    public Optional<PlaySite> getPlayGround(UUID id) {
        return playGroundRepository.findById(id);
    }

    public PlaySite updatePlayGround(UUID id, PlayGroundRequest playGroundRequest) {
        PlaySite existing = playGroundRepository.findById(id).orElseThrow();
        update(playGroundRequest, existing);
        return playGroundRepository.save(existing);
    }

    private void update(PlayGroundRequest playGroundRequest, PlaySite existing) {
        List<Attraction> attractions = new ArrayList<>();
        playGroundRequest.attractions().forEach(a -> attractions.add(new Attraction(a.type(), a.capacity())));
        existing.setName(playGroundRequest.name());
        existing.getKids().clear();
        existing.getQueue().clear();
        existing.getAttractions().clear();
        existing.getAttractions().addAll(attractions);
    }

    public void deletePlayGround(UUID id) {
        playGroundRepository.delete(id);
    }

    public boolean addKid(UUID playGroundId, KidRequest kidRequest) {
        if (!playGroundRepository.validTicket(kidRequest.ticketNumber())) {
            return false;
        }
        PlaySite playSite = playGroundRepository.findById(playGroundId).orElseThrow();
        Kid kid = new Kid(kidRequest.name(), kidRequest.age(), kidRequest.ticketNumber());
        return playSite.addKid(kid, kidRequest.acceptQueue());
    }

    public void removeKid(UUID playGroundId, String ticketNumber) {
        PlaySite playSite = playGroundRepository.findById(playGroundId).orElseThrow();
        playSite.removeKid(ticketNumber);
    }

    public int getUtilization(UUID playGroundId) {
        PlaySite ps = playGroundRepository.findById(playGroundId).orElseThrow();
        return ps.getUtilization();
    }
    public int getTotalVisitorsToday() {
        return playGroundRepository.findAll().stream().mapToInt(PlaySite::getTotalVisitorsToday).sum();
    }
}
