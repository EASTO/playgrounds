package org.homework.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class PlaySite {
    private final UUID id;
    @Setter
    private String name;
    private List<Attraction> attractions;
    private Set<Kid> kids = new HashSet<>();
    private Queue<Kid> queue = new LinkedList<>();
    private int totalVisitorsToday = 0;

    public PlaySite(String name, List<Attraction> attractions) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.attractions = attractions;
    }

    public int getCapacity() {
        return attractions.stream().mapToInt(Attraction::capacity).sum();
    }

    public int getUtilization() {
        return (int) ((kids.size() * 100.0) / getCapacity());
    }

    public boolean addKid(Kid kid, boolean acceptQueue) {
        if (kids.size() < getCapacity()) {
            kids.add(kid);
            incrementVisitors();
            return true;
        } else if (acceptQueue) {
            queue.add(kid);
            return false;
        }
        return false;
    }

    public void removeKid(String ticketNumber) {
        kids.removeIf(k -> k.ticketNumber().equals(ticketNumber));
        while (kids.size() < getCapacity() && !queue.isEmpty()) {
            Kid next = queue.poll();
            if (next != null) {
                kids.add(next);
                incrementVisitors();
            }
        }
        queue.removeIf(k -> k.ticketNumber().equals(ticketNumber));
    }

    private void incrementVisitors() { totalVisitorsToday++; }
}
