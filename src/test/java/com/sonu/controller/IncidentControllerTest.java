package com.sonu.controller;

import com.sonu.dto.IncidentHistoryResponse;
import com.sonu.dto.IncidentResponse;
import com.sonu.dto.IncidentStatusResponse;
import com.sonu.dto.UpdateIncidentRequest;
import com.sonu.dto.UpdateIncidentStatusRequest;
import com.sonu.enums.IncidentCategory;
import com.sonu.enums.IncidentSeverity;
import com.sonu.enums.IncidentStatus;
import com.sonu.exceptions.GlobalExceptionHandler;
import com.sonu.service.IncidentService;

import org.junit.jupiter.api.Test;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentController.class)
@Import(GlobalExceptionHandler.class)
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentService incidentService;

    @Test
    void createIncident_shouldReturn201() throws Exception {

        IncidentResponse response = new IncidentResponse(
                1L,
                "Major fire",
                "Fire reported",
                IncidentCategory.OTHER,
                IncidentSeverity.LOW,
                16.3,
                80.43,
                IncidentStatus.REPORTED,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(incidentService.createIncident(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Major fire",
                            "description": "Fire reported",
                            "latitude": 16.3,
                            "longitude": 80.43
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("REPORTED"));
    }

    @Test
    void getAllIncidents_shouldReturn200() throws Exception {

        when(incidentService.getAllIncidents())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk());
    }

    @Test
    void getIncidentById_shouldReturn200() throws Exception {

        IncidentResponse response = new IncidentResponse(
                1L,
                "Major fire",
                "Fire reported",
                IncidentCategory.FIRE,
                IncidentSeverity.CRITICAL,
                16.3,
                80.43,
                IncidentStatus.ANALYZED,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(incidentService.getIncidentById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("FIRE"))
                .andExpect(jsonPath("$.severity").value("CRITICAL"));
    }

    @Test
    void updateIncident_shouldReturn200() throws Exception {

        IncidentResponse response = new IncidentResponse(
                1L,
                "Updated fire",
                "Updated description",
                IncidentCategory.FIRE,
                IncidentSeverity.CRITICAL,
                16.3,
                80.43,
                IncidentStatus.ANALYZED,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(incidentService.updateIncident(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/incidents/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Updated fire",
                            "description": "Updated description",
                            "latitude": 16.3,
                            "longitude": 80.43
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated fire"));
    }

    @Test
    void updateIncidentStatus_shouldReturn200() throws Exception {

        IncidentStatusResponse response = new IncidentStatusResponse(
                1L,
                "ANALYZING",
                LocalDateTime.now());

        when(incidentService.updateIncidentStatus(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/incidents/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "status": "ANALYZING"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ANALYZING"));
    }

    @Test
    void getIncidentHistory_shouldReturn200() throws Exception {

        IncidentHistoryResponse history = new IncidentHistoryResponse(
                "REPORTED",
                "ANALYZING",
                LocalDateTime.now());

        when(incidentService.getIncidentHistory(1L))
                .thenReturn(List.of(history));

        mockMvc.perform(get("/api/incidents/1/history"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].previousStatus")
                                .value("REPORTED"))
                .andExpect(
                        jsonPath("$[0].newStatus")
                                .value("ANALYZING"));
    }

    @Test
    void createIncident_withInvalidRequest_shouldReturn400()
            throws Exception {

        mockMvc.perform(post("/api/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "",
                            "description": "",
                            "latitude": null,
                            "longitude": null
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}