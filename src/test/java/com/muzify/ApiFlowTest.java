package com.muzify;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiFlowTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private String token(String user, String pass) throws Exception {
        String body = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + om.readTree(body).get("token").asText();
    }

    @Test
    void favoritesRequireAuthorization() throws Exception {
        mvc.perform(get("/api/favorites"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void favoritesFlow() throws Exception {
        String t = token("user", "user123");
        mvc.perform(post("/api/favorites/1").header("Authorization", t)).andExpect(status().isCreated());
        mvc.perform(post("/api/favorites/1").header("Authorization", t)).andExpect(status().isConflict());
        mvc.perform(post("/api/favorites/9999").header("Authorization", t)).andExpect(status().isNotFound());
        mvc.perform(get("/api/favorites").header("Authorization", t)).andExpect(status().isOk());
        mvc.perform(delete("/api/favorites/1").header("Authorization", t)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/favorites/1").header("Authorization", t)).andExpect(status().isNotFound());
    }

    @Test
    void listeningFlow() throws Exception {
        String t = token("user", "user123");
        mvc.perform(post("/api/listening").header("Authorization", t)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"trackId\":2}"))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/listening").header("Authorization", t)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
        mvc.perform(post("/api/listening").header("Authorization", t)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"trackId\":9999}"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/listening/history").header("Authorization", t)).andExpect(status().isOk());
    }

    @Test
    void rolesAreEnforced() throws Exception {
        String user = token("user", "user123");
        String admin = token("admin", "admin123");
        String body = "{\"title\":\"T\",\"artist\":\"A\"}";
        mvc.perform(post("/api/tracks").header("Authorization", user)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/users").header("Authorization", user))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/users").header("Authorization", admin))
                .andExpect(status().isOk());
    }

    @Test
    void audioIsStreamable() throws Exception {
        String t = token("user", "user123");
        mvc.perform(get("/api/tracks/1/stream").header("Authorization", t))
                .andExpect(status().isOk());
        mvc.perform(get("/api/tracks/1/stream")).andExpect(status().isUnauthorized());
    }

    @Test
    void playerPageIsPublic() throws Exception {
        mvc.perform(get("/index.html")).andExpect(status().isOk());
    }
}
