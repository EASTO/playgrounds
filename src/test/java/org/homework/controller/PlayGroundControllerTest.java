package org.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.homework.domain.Attraction;
import org.homework.domain.PlaySite;
import org.homework.request.AttractionRequest;
import org.homework.request.KidRequest;
import org.homework.request.PlayGroundRequest;
import org.homework.service.PlayGroundService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayGroundController.class)
class PlayGroundControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PlayGroundService playGroundService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void create_returnsPlaySite() throws Exception {
        PlayGroundRequest req = new PlayGroundRequest("Fun Park", List.of(new AttractionRequest(Attraction.Type.SLIDE, 5)));
        PlaySite site = new PlaySite("Fun Park", List.of(new Attraction(Attraction.Type.SLIDE, 5)));
        Mockito.when(playGroundService.createPlayGround(any())).thenReturn(site);

        mockMvc.perform(post("/api/v1/playsites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fun Park"));
    }

    @Test
    void get_returnsPlaySite() throws Exception {
        UUID id = UUID.randomUUID();
        PlaySite site = new PlaySite("Test", List.of(new Attraction(Attraction.Type.SLIDE, 1)));
        Mockito.when(playGroundService.getPlayGround(eq(id))).thenReturn(Optional.of(site));

        mockMvc.perform(get("/api/v1/playsites/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void addKid_returnsKidAdded() throws Exception {
        UUID id = UUID.randomUUID();
        KidRequest kidReq = new KidRequest("Alice", 7, "T1", false);
        Mockito.when(playGroundService.addKid(eq(id), any())).thenReturn(true);

        mockMvc.perform(post("/api/v1/playsites/" + id + "/kids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kidReq)))
                .andExpect(status().isOk())
                .andExpect(content().string("Kid added"));
    }

    @Test
    void addKid_returnsBadRequestForInvalidParams() throws Exception {
        UUID id = UUID.randomUUID();
        KidRequest invalidKid = new KidRequest("", -1, "", false);

        mockMvc.perform(post("/api/v1/playsites/" + id + "/kids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidKid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name cannot be null or blank"))
                .andExpect(jsonPath("$.age").value("Age cannot be negative"))
                .andExpect(jsonPath("$.ticketNumber").value("Ticket number cannot be null or blank"));
    }

    @Test
    void create_returnsBadRequestForInvalidAttractionRequest() throws Exception {
        AttractionRequest invalidAttraction = new AttractionRequest(null, 0);
        PlayGroundRequest req = new PlayGroundRequest("", List.of(invalidAttraction));

        mockMvc.perform(post("/api/v1/playsites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Playground name cannot be null or blank"))
                .andExpect(jsonPath("$.attractions[0].type").value("Attraction type cannot be null"))
                .andExpect(jsonPath("$.attractions[0].capacity").value("Attraction capacity must be greater than zero"));
    }

    @Test
    void utilization_returnsUtilization() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(playGroundService.getUtilization(eq(id))).thenReturn(50);

        mockMvc.perform(get("/api/v1/playsites/" + id + "/utilization"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    void totalVisitors_returnsSum() throws Exception {
        Mockito.when(playGroundService.getTotalVisitorsToday()).thenReturn(3);

        mockMvc.perform(get("/api/v1/playsites/visitors"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
