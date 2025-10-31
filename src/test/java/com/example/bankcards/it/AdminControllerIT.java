package com.example.bankcards.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test") // добавил: загружает реальный application-test.yml с postgres
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(Lifecycle.PER_CLASS)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String ADMIN_BASE = "/api/admin/cards";
    private static final Long EXISTING_CARD_ID = 1L;
    private static final Long NON_EXISTENT_CARD_ID = 999L;

    @BeforeAll
    void setUp() throws Exception {
        executeSqlScript(jdbcTemplate, "001_init_test_schema.sql");
        executeSqlScript(jdbcTemplate, "002_init_test_data.sql");
    }

    @AfterAll
    void tearDown() throws Exception {
        executeSqlScript(jdbcTemplate, "003_drop_test_schema.sql");
    }

    private void executeSqlScript(JdbcTemplate jdbcTemplate, String scriptName) throws Exception {
        String sql = loadSqlScript(scriptName);
        for (String statement : sql.split(";")) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                jdbcTemplate.execute(trimmed);
            }
        }
    }

    private String loadSqlScript(String scriptName) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db/migration/" + scriptName)) {
            if (is == null) {
                throw new IllegalArgumentException("Файл не найден: " + scriptName);
            }
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllCards_Success() throws Exception {
        mockMvc.perform(get(ADMIN_BASE)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(4))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].number").value("4111********1111"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllCards_InvalidPage_ThrowsBadRequest() throws Exception {
        mockMvc.perform(get(ADMIN_BASE)
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.page").value("Номер страницы должен быть неотрицательным"));
    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllCards_InvalidSize_ThrowsBadRequest() throws Exception {
        mockMvc.perform(get(ADMIN_BASE)
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size").value("Размер страницы должен быть положительным"));
    }

    @Test
    @Order(4)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllCards_InvalidSortBy_ThrowsBadRequest() throws Exception {
        mockMvc.perform(get(ADMIN_BASE)
                        .param("sortBy", "invalid@field"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.sortBy").value("Поле сортировки содержит недопустимые символы"));
    }

    @Test
    @Order(5)
    @WithMockUser(username = "user", roles = "USER")
    void getAllCards_Unauthorized_ThrowsForbidden() throws Exception {
        mockMvc.perform(get(ADMIN_BASE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
    }

    @Test
    @Order(6)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void blockCard_Success() throws Exception {
        mockMvc.perform(post(ADMIN_BASE + "/block/" + EXISTING_CARD_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Карта с ID " + EXISTING_CARD_ID + " успешно заблокирована"));
    }

    @Test
    @Order(7)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void blockCard_AlreadyBlocked_ThrowsBadRequest() throws Exception {
        mockMvc.perform(post(ADMIN_BASE + "/block/" + EXISTING_CARD_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Карта с ID " + EXISTING_CARD_ID + " уже заблокирована"));
    }

    @Test
    @Order(8)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void blockCard_NotFound_ThrowsNotFound() throws Exception {
        mockMvc.perform(post(ADMIN_BASE + "/block/" + NON_EXISTENT_CARD_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Карта с ID " + NON_EXISTENT_CARD_ID + " не найдена"));
    }

    @Test
    @Order(9)
    @WithMockUser(username = "user", roles = "USER")
    void blockCard_Unauthorized_ThrowsForbidden() throws Exception {
        mockMvc.perform(post(ADMIN_BASE + "/block/" + EXISTING_CARD_ID))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
    }

    @Test
    @Order(10)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void unblockCard_Success() throws Exception {
        mockMvc.perform(post(ADMIN_BASE + "/unblock/" + EXISTING_CARD_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("Карта с ID " + EXISTING_CARD_ID + " успешно разблокирована"));
    }

    @Test
    @Order(11)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void unblockCard_AlreadyActive_ThrowsBadRequest() throws Exception {
        mockMvc.perform(post(ADMIN_BASE + "/unblock/" + EXISTING_CARD_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Карта с ID " + EXISTING_CARD_ID + " уже активна"));
    }

    @Test
    @Order(12)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void unblockCard_NotFound_ThrowsNotFound() throws Exception {
        mockMvc.perform(post(ADMIN_BASE + "/unblock/" + NON_EXISTENT_CARD_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Карта с ID " + NON_EXISTENT_CARD_ID + " не найдена"));
    }

    @Test
    @Order(13)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCardDetails_Success() throws Exception {
        mockMvc.perform(get(ADMIN_BASE + "/" + EXISTING_CARD_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXISTING_CARD_ID.intValue()))
                .andExpect(jsonPath("$.number").value("4111********1111"))
                .andExpect(jsonPath("$.balance").value(10000.00))
                .andExpect(jsonPath("$.status").value("BLOCKED")); // после block
    }

    @Test
    @Order(14)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCardDetails_NotFound_ThrowsNotFound() throws Exception {
        mockMvc.perform(get(ADMIN_BASE + "/" + NON_EXISTENT_CARD_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Карта с ID " + NON_EXISTENT_CARD_ID + " не найдена"));
    }

    @Test
    @Order(15)
    @WithMockUser(username = "user", roles = "USER")
    void getCardDetails_Unauthorized_ThrowsForbidden() throws Exception {
        mockMvc.perform(get(ADMIN_BASE + "/" + EXISTING_CARD_ID))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
    }

    @Test
    @Order(16)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCardBalance_Success() throws Exception {
        String request = "{\"balance\": 999.99}";

        mockMvc.perform(patch(ADMIN_BASE + "/balance/" + EXISTING_CARD_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(content().string("Баланс карты с ID " + EXISTING_CARD_ID + " обновлён до 999.99"));
    }

    @Test
    @Order(17)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCardBalance_Negative_ThrowsBadRequest() throws Exception {
        String request = "{\"balance\": -100}";

        mockMvc.perform(patch(ADMIN_BASE + "/balance/" + EXISTING_CARD_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.balance").value("Баланс не может быть отрицательным"));
    }

    @Test
    @Order(18)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCardBalance_NotFound_ThrowsNotFound() throws Exception {
        String request = "{\"balance\": 100}";

        mockMvc.perform(patch(ADMIN_BASE + "/balance/" + NON_EXISTENT_CARD_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Карта с ID " + NON_EXISTENT_CARD_ID + " не найдена"));
    }

    @Test
    @Order(19)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCardBalance_InvalidJson_ThrowsBadRequest() throws Exception {
        mockMvc.perform(patch(ADMIN_BASE + "/balance/" + EXISTING_CARD_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(20)
    void noAuth_AccessDenied() throws Exception {
        mockMvc.perform(get(ADMIN_BASE))
                .andExpect(status().isUnauthorized());
    }
}