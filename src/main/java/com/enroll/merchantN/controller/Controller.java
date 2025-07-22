package com.enroll.merchantN.controller;

import com.enroll.merchantN.entity.sqlMerchant.FileDetailsRepo;
import com.enroll.merchantN.helper.ValidateFile;
import com.enroll.merchantN.service.KafkaProducerService;
import com.enroll.merchantN.service.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/merchant")
public class Controller {
    @Value("${VALIDATE_FILE}")
    private String validationRequired;
    @Autowired
    private ValidateFile extract;
    @Autowired
    private ServiceImpl serviceImpl;
    @Autowired
    private FileDetailsRepo fileDetailsRepo;


    @PostMapping("/validateFile")
    public String validateFile(@RequestParam("file") MultipartFile file,
                                @RequestParam("userId") String userId) {
        JSONObject validateFileResponse = new JSONObject();
        String contentType=file.getContentType();
//        if ("false".equalsIgnoreCase(validationRequired) || file == null || file.isEmpty()) {
//            validateFileResponse.put("status", "validation failed or file not found");
//        }
//        else {
            return serviceImpl.validateAndSaveCsv(file,userId).toString();
//        }
//        return validateFileResponse.toString();
    }
    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/processCsv")
    public String processCsv(@RequestParam("fileId") String fileId){
        JSONObject response=new JSONObject();
        serviceImpl.processCsv(fileId);
        response.put("code",200);
        response.put("status","inProcess");
        log.info("processing file");
        return response.toString();
    }
    @GetMapping("/listAllData")
    public String listAllData(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                @RequestParam("userId")String userId){
        log.info("list all data");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created_on"));
        Map<String, Object> response = serviceImpl.listAllData(pageable,userId);
        return response.toString();
    }
    @GetMapping("/downloadUpdatedFile")
    public ResponseEntity<Resource> downloadUpdatedFile(@RequestParam("fileId") String fileId) {
       return serviceImpl.downloadFile(fileId);
    }
    @GetMapping("/downloadOriginalFile")
    public ResponseEntity<Resource> downloadOriginalFile(@RequestParam("fileId") String fileId) {
        return serviceImpl.downloadOriginalFile(fileId);
    }
    @GetMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/merchant_data")
    public ResponseEntity<String> merchant_data(@RequestParam String message) {
        if(message.isEmpty()) return ResponseEntity.noContent().build();
        return serviceImpl.findMerchantData(message);
    }
    @GetMapping("/wallet-number")
    public ResponseEntity<String> merchant_wallet_number(@RequestParam String message) {
        if(message.isEmpty()) return ResponseEntity.noContent().build();
        return serviceImpl.findWalletNumber(message);
    }

}
