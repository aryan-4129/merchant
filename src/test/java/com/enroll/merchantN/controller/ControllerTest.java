package com.enroll.merchantN.controller;

import com.enroll.merchantN.helper.ValidateFile;
import com.enroll.merchantN.service.ServiceImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ValidateFile extract;

    @Mock
    private ServiceImpl serviceImpl;

    @InjectMocks
    private Controller controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testValidateFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", "name,email\njohn,john@example.com".getBytes());

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("status", "inProcess");

        when(serviceImpl.validateAndSaveCsv(any(), anyString())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(multipart("/merchant/validateFile")
                        .file(file)
                        .param("userId", "123"))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse.toString()));
    }
}
