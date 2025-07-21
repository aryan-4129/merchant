package com.enroll.merchantN.controller;

import com.enroll.merchantN.dto.FileRequest;
import com.enroll.merchantN.helper.ValidateFile;
import com.enroll.merchantN.service.ServiceImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class ControllerTest {

   private MockMvc mockMvc;

   @Mock
   private ValidateFile extract;

   @Mock
   private ServiceImpl service;

   @InjectMocks
   private Controller controller;

   @BeforeEach
   void setUp() {
       mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
   }

   @Test
   void testAddFile() throws Exception {
       // Arrange: Prepare mock behavior for service.addFile()
       FileRequest fileRequest = new FileRequest();  // Populate the fileRequest if necessary
       fileRequest.setPath("somePath");
       fileRequest.setStatus("someStatus");

       // Mock the service response
       JSONObject mockResponse = new JSONObject();
       mockResponse.put("status", "inProcess");
       when(service.addFile(fileRequest)).thenReturn(mockResponse);

       // Act & Assert: Perform the POST request and assert status and response
       mockMvc.perform(post("/saveFile")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"path\":\"somePath\",\"status\":\"someStatus\"}"))  // Sample JSON input
               .andExpect(status().isOk())  // Assert that the status is OK (200)
               .andExpect(content().json(mockResponse.toString()));  // Assert that the response matches the mock response
   }

   @Test
   void testAddFileWithNullRequest() throws Exception {
       // Arrange: Mock behavior when the service returns a null response (e.g., status "failed")
       FileRequest fileRequest = null;

       when(service.addFile(fileRequest)).thenReturn("{\"status\":\"failed\"}");

       // Act & Assert: Perform the POST request with null input and assert failure response
       mockMvc.perform(post("/saveFile")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))  // Empty JSON for null fileRequest
               .andExpect(status().isOk())  // Assert that the status is OK (200)
               .andExpect(content().json("{\"status\":\"failed\"}"));  // Assert that the response is failed
   }
}
