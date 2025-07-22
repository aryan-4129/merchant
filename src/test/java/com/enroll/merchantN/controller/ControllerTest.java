package com.enroll.merchantN.controller;

import com.enroll.merchantN.service.ServiceImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import com.enroll.merchantN.helper.ValidateFile;
import com.enroll.merchantN.entity.sqlMerchant.FileDetailsRepo;
import com.enroll.merchantN.entity.sqlMerchant.MerchantDetailsRepo;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceImpl serviceImpl;

    @MockBean
    private ValidateFile validateFile;

    @MockBean
    private FileDetailsRepo fileDetailsRepo;

    @MockBean
    private MerchantDetailsRepo merchantDetailsRepo;

    @Test
    void testValidateFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "col1,col2\nval1,val2".getBytes());

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("status", "ok");

        Mockito.when(serviceImpl.validateAndSaveCsv(any(), eq("user123")))
               .thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/merchant/validateFile")
                .file(file)
                .param("userId", "user123")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse.toString()));
    }

    @Test
    void testHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/merchant/home"))
                .andExpect(status().isOk())
                .andExpect(content().string("home"));
    }

    @Test
    void testProcessCsv() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/merchant/processCsv")
                        .param("fileId", "file123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("inProcess"));
    }

    @Test
    void testListAllData() throws Exception {
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("key", "value");

        Mockito.when(serviceImpl.listAllData(any(), eq("user123"))).thenReturn(mockData);

        mockMvc.perform(MockMvcRequestBuilders.get("/merchant/listAllData")
                        .param("userId", "user123")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockData.toString()));
    }

    @Test
    void testDownloadUpdatedFile() throws Exception {
        Resource file = new ByteArrayResource("data".getBytes());

        Mockito.when(serviceImpl.downloadFile("file123")).thenReturn(ResponseEntity.ok(file));

        mockMvc.perform(MockMvcRequestBuilders.get("/merchant/downloadUpdatedFile")
                        .param("fileId", "file123"))
                .andExpect(status().isOk());
    }

    @Test
    void testDownloadOriginalFile() throws Exception {
        Resource file = new ByteArrayResource("data".getBytes());

        Mockito.when(serviceImpl.downloadOriginalFile("file123")).thenReturn(ResponseEntity.ok(file));

        mockMvc.perform(MockMvcRequestBuilders.get("/merchant/downloadOriginalFile")
                        .param("fileId", "file123"))
                .andExpect(status().isOk());
    }

    @Test
    void testMerchantData() throws Exception {
        Mockito.when(serviceImpl.findMerchantData("abc")).thenReturn(ResponseEntity.ok("merchant-data"));

        mockMvc.perform(MockMvcRequestBuilders.get("/merchant/merchant_data")
                        .param("message", "abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("merchant-data"));
    }

    @Test
    void testWalletNumber() throws Exception {
        Mockito.when(serviceImpl.findWalletNumber("wallet123")).thenReturn(ResponseEntity.ok("wallet-number"));

        mockMvc.perform(MockMvcRequestBuilders.get("/merchant/wallet-number")
                        .param("message", "wallet123"))
                .andExpect(status().isOk())
                .andExpect(content().string("wallet-number"));
    }

    @Test
    void testWalletNumberEmptyMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/merchant/wallet-number")
                        .param("message", ""))
                .andExpect(status().isNoContent());
    }
}
