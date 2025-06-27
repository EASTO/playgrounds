package org.homework.controller;

import jakarta.validation.Valid;
import org.homework.domain.PlaySite;
import org.homework.request.KidRequest;
import org.homework.request.PlayGroundRequest;
import org.homework.service.PlayGroundService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/playsites")
public class PlayGroundController {

    private final PlayGroundService playGroundService;

    public PlayGroundController(PlayGroundService playGroundService) {
        this.playGroundService = playGroundService;
    }

    @PostMapping
    public PlaySite create(@RequestBody @Valid PlayGroundRequest request) {
        return playGroundService.createPlayGround(request);
    }

    @GetMapping("/{id}")
    public PlaySite get(@PathVariable UUID id) {
        return playGroundService.getPlayGround(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public PlaySite update(@PathVariable UUID id, @RequestBody @Valid PlayGroundRequest request) {
        return playGroundService.updatePlayGround(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        playGroundService.deletePlayGround(id);
    }

    @PostMapping("/{id}/kids")
    public String addKid(@PathVariable UUID id, @RequestBody @Valid KidRequest request) {
        return playGroundService.addKid(id, request) ? "Kid added" : "Kid queued or rejected";
    }

    @DeleteMapping("/{id}/kids/{ticketNumber}")
    public void removeKid(@PathVariable UUID id, @PathVariable String ticketNumber) {
        playGroundService.removeKid(id, ticketNumber);
    }

    @GetMapping("/{id}/utilization")
    public int utilization(@PathVariable UUID id) {
        return playGroundService.getUtilization(id);
    }

    @GetMapping("/visitors")
    public int totalVisitors() {
        return playGroundService.getTotalVisitorsToday();
    }
}
