package com.enroll.merchantN.service;


import com.enroll.merchantN.dto.Response;
import com.enroll.merchantN.dto.qrResponse.QrResult;
import com.enroll.merchantN.dto.userResponse.UserResult;
import com.enroll.merchantN.entity.mongoMerchant.MerchantData;
import com.enroll.merchantN.entity.mongoMerchant.MerchantDataRepo;
import com.enroll.merchantN.entity.sqlMerchant.FileDetails;
import com.enroll.merchantN.entity.sqlMerchant.FileDetailsRepo;
import com.enroll.merchantN.helper.Helper;
import com.enroll.merchantN.helper.Utils;
import com.enroll.merchantN.helper.ValidateFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.enroll.merchantN.helper.ValidateFile.isCellEmpty;

/**
 * @author raghav
 */
@Service
@Slf4j
public class ServiceImpl {
    @Value("${FOLDER_PATH}")
    private String folderPath;
    @Autowired
    private ValidateFile extract;
    @Value("${checked.parameter}")
    private String[] checkedParameter;
    @Autowired
    private FileDetailsRepo fileDetailsRepo;
    @Autowired
    private Helper helper;
    @Autowired
    private MerchantDataRepo merchantDataRepo;
    @Autowired
    private ObjectMapper obj;

    @Autowired
    private Utils utils;
    private static LinkedHashMap<String, Integer> hash = new LinkedHashMap<>();
//    int totalRows=0;
//    int faultyRows=0;
    public JSONObject validateAndSaveCsv(MultipartFile file,String userId) {
        String filePath = folderPath + file.getOriginalFilename();
        JSONObject responseJson = new JSONObject();
        log.info(filePath);
        FileDetails details = new FileDetails();
        try {
            String fileId = Utils.generateId("MCS");
            details.setFile_status("InValidation");
            details.setPath(filePath);
            details.setId(fileId);
            details.setCreated_by(userId);
            details.setCreated_on(new Date());
            file.transferTo(new File(filePath));
            fileDetailsRepo.save(details);
            FileDetails fileDetails = fileDetailsRepo.findById(fileId).orElseThrow(FileNotFoundException::new);
            responseJson= validateFile(fileDetails.getId());
            responseJson.put("fileId",fileId);
        } catch (Exception e) {
            log.error("error while validating file: {}",e.getMessage());
            responseJson.put("status", HttpServletResponse.SC_BAD_REQUEST);
        }
        return responseJson;
    }
    private String getCellValue(Cell cell) {
        log.debug("get cell value 2");
        DecimalFormat df = new DecimalFormat("0");
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return df.format(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (cell.getCellType() != CellType.BLANK &&
                    !(cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty())) {
                return false;
            }
        }
        return true;
    }

//    public JSONObject validateCsv(String path) {
//        HashMap<String,List<Integer>> hash2=new HashMap<>();
//        JSONObject response = new JSONObject();
//        try (FileInputStream fis = new FileInputStream(new File(path));
//             Workbook workbook = new XSSFWorkbook(fis)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            Row firstRow = sheet.getRow(0);
//            hash.clear();
//            for (int i = firstRow.getFirstCellNum(); i <= firstRow.getLastCellNum(); i++) {
//                Cell cell = firstRow.getCell(i);
//                if (!isCellEmpty(cell)) {
//                    hash.put(cell.getStringCellValue(), i);
//                }
//            }
//            log.info(hash.toString());
//            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
//                Row row = sheet.getRow(i);
//                if (row == null || isRowEmpty(row)) continue;
//
//                totalRows++;
//                boolean hasError = false;
//
//                for (String column : checkedParameter) {
//                    Integer columnIndex = hash.get(column);
//                    if (columnIndex == null) continue;  // Column not found, ignore
//                    Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//                    if (isCellEmpty(cell)) {
//                        log.error("Error: Mandatory column '{}' is empty at row {}", column, i);
//                        hash2.computeIfAbsent(column, k -> new ArrayList<>()).add(i);
//                        hasError = true;
//                    }
//                }
//
//                if (hasError) {
//                    faultyRows++;
//
//                }
//            }
//
//            response.put("total Rows", totalRows);
//            response.put("valid Rows", totalRows - faultyRows);
//            response.put("faulty Rows", faultyRows);
//            response.put("null columns",hash2);
//        } catch (Exception e) {
//            log.error("Error in validateCsv: {}", e.getMessage(), e);
//        }
//
//        return response;
//
//    }

    @Async
    public void processCsv(String fileId) {
        try {
            FileDetails fileDetails = fileDetailsRepo.findById(fileId).orElseThrow(() ->
                    new FileSystemNotFoundException("file id not found: " + fileId));
            extract.extractFile(fileDetails);
        } catch (FileNotFoundException fse) {
            log.error("file not found: {}", fileId);
        } catch (Exception e) {
            log.error("unhandled exception occured");

        }
    }

    public Map<String, Object> listAllData(Pageable pageable, String userId) {
        Page<FileDetails> paginatedFiles;

        // Fetch data based on userId
        if (userId != null && !userId.isEmpty()) {
            paginatedFiles = fileDetailsRepo.findFileWithUserId(userId, pageable);
        } else {
            log.error("user id is null");
            return null;
        }

        List<JSONObject> filesList = paginatedFiles.getContent().stream().map(file -> {
            JSONObject fileData = new JSONObject();
            fileData.put("fileId", file.getId());
            fileData.put("uploadedAt", file.getCreated_on());
            fileData.put("fileName", file.getPath());
            fileData.put("status", file.getFile_status());
            return fileData;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("files", filesList);
        response.put("totalPages", paginatedFiles.getTotalPages());
        response.put("totalElements", paginatedFiles.getTotalElements());

        return response;
    }


    public ResponseEntity<Resource> downloadFile(String fileId) {
        try {
            Optional<FileDetails> fileDetails = fileDetailsRepo.findById(fileId);
            if (fileDetails.isEmpty()) throw new RuntimeException("File details not found");
            else {
                File file = new File(fileDetails.get().getClone_path());
                if (!file.exists()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                Resource resource = new FileSystemResource(file);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename(file.getName()).build());

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            }
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


    }

    public ResponseEntity<Resource> downloadOriginalFile(String fileId) {
        try {
            Optional<FileDetails> fileDetails = fileDetailsRepo.findById(fileId);
            if (fileDetails.isEmpty()) throw new RuntimeException("File details not found");
            else {
                File file = new File(fileDetails.get().getPath());
                if (!file.exists()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                Resource resource = new FileSystemResource(file);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename(file.getName()).build());
                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            }
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    public ResponseEntity<String> findMerchantData(String message) {
        JSONObject responseJson=new JSONObject();
        log.info("entering find Merchant data: "+message);
        try {
            Response<QrResult> qrResponseDto;
            Response<UserResult> userResultResponse;
            Long phoneNo = Long.parseLong(message);
            MerchantData merchantData = merchantDataRepo.findById(phoneNo).get();
            String qrResponse = merchantData.getQr_response();

            String userResponse = merchantData.getUser_enq_response();
            log.info("merch data: {}",merchantData);
            log.info("qr dto: {}",qrResponse);
            log.info("qr dto: {}",userResponse);
            qrResponseDto = obj.readValue(
                    qrResponse,
                    new TypeReference<Response<QrResult>>() {});
            userResultResponse = obj.readValue(
                    userResponse,
                    new TypeReference<Response<UserResult>>() {});
            log.info("qr dto: {}",qrResponseDto);
            log.info("user dto: {}",userResultResponse);
            responseJson.put("qrString", qrResponseDto.getResult().getQrCode());
            responseJson.put("merchantName", merchantData.getFile_data().getMerchantName());
            responseJson.put("tillNo",merchantData.getFile_data().getTillNumber());
            responseJson.put("walletNo",merchantData.getFile_data().getWalletNumMsisdn());
            responseJson.put("businessName",merchantData.getFile_data().getBusinessNameDisplay());
            responseJson.put("msisdn",merchantData.getFile_data().getSimNumber());
            responseJson.put("country",merchantData.getFile_data().getCountry());
            responseJson.put("currency",merchantData.getFile_data().getCurrency());
            responseJson.put("city",merchantData.getFile_data().getCity());
            responseJson.put("postalCode",merchantData.getFile_data().getPostalCode());
            return ResponseEntity.ok(responseJson.toString());
        }catch (JsonProcessingException json){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (Exception e){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }
    public ResponseEntity<String> findWalletNumber(String message){
        JSONObject response=new JSONObject();
        try {
            MerchantData merchantData = merchantDataRepo.findBySimNumber(message);
            response.put("walletNo", merchantData.getMerchant_msisdn());
            return ResponseEntity.ok(response.toString());
        }catch (Exception e){
            log.error("details not found");
            return ResponseEntity.badRequest().body("Details not found");
        }


    }
    public JSONObject validateFile(String fileId) {
        JSONObject response = new JSONObject();
        List<JSONObject> processedRows = new ArrayList<>();
        HashMap<String, List<Integer>> nullColumns = new HashMap<>();
        int totalRows = 0;
        int faultyRows = 0;

        FileDetails fileDetails = fileDetailsRepo.findById(fileId).orElse(null);
        if (fileDetails == null) {
            response.put("error", "File not found");
            return response;
        }

        try (FileInputStream fis = new FileInputStream(new File(fileDetails.getPath()));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                response.put("error", "Empty file");
                return response;
            }

            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                headers.add(header);
                columnIndexMap.put(header, cell.getColumnIndex());
            }
            log.info("headers name: {}",headers);
            log.info("headers with mapping: {}",columnIndexMap);

            while (rowIterator.hasNext()) {
                log.debug("reading rows");
                Row row = rowIterator.next();
                if (isRowEmpty(row)) continue;

                totalRows++;
                JSONObject rowJson = new JSONObject();
                List<String> messages = new ArrayList<>();
                boolean isValid = true;

                for (String column : checkedParameter) {
                    log.debug("checking params");
                    int columnIndex = columnIndexMap.getOrDefault(column, -1);
                    if (columnIndex == -1) continue;

                    Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = getCellValue(cell);
                    rowJson.put(column, cellValue);

                    if (isCellEmpty(cell)) {
                        log.debug("null check");
                        nullColumns.computeIfAbsent(column, k -> new ArrayList<>()).add(row.getRowNum());
                        messages.add(column + " is empty");
                        isValid = false;
                    } else {
                        if (column.equalsIgnoreCase("Wallet Num (MSISDN)") && (!helper.validMsisdn(cellValue) && !helper.checkInDbForWallet(cellValue))) {
                            log.debug("wallet number");
                            if(helper.checkInDbForWallet(cellValue)){
                                messages.add("Wallet already exists in our system");
                            }else messages.add("Invalid wallet number");
                            isValid=false;
                        }
                        else if (column.equalsIgnoreCase("email") && !helper.validEmail(cellValue)) {
                            log.debug("email");
                            messages.add("Invalid email");
                            isValid = false;
                        } else if (column.equalsIgnoreCase("wallet type") && !helper.validWalletType(cellValue)) {
                            log.debug("wallet_type");
                            messages.add("Invalid wallet type");
                            isValid = false;
                        } else if (column.equalsIgnoreCase("TILL Number") && !helper.validTillNumber(cellValue)) {
                            log.debug("invalid till number");
                            messages.add("Invalid till number");
                            isValid = false;
                        } else if (column.equalsIgnoreCase("Prefered Language") && !helper.validPreferredLanguage(cellValue)) {
                            log.debug("invalid language");
                            messages.add("Invalid preferred language");
                            isValid = false;
                        } else if (column.equalsIgnoreCase("Service Fee type") && !helper.validFeeType(cellValue)) {
                            log.debug("service type");
                            messages.add("Invalid service fee type");
                            isValid = false;
                        } else if (column.equalsIgnoreCase("Merchant type") && !helper.validMerchantType(cellValue)) {
                            log.debug("merchant_type");
                            messages.add("Invalid merchant type");
                            isValid = false;
                        }else if (column.equalsIgnoreCase("SIM number") && (!helper.validSimNumber(cellValue) || helper.checkInDb(cellValue))){
                            log.debug("sim number");
                            if(helper.checkInDb(cellValue)){
                                messages.add("User already exists in our system");
                            }else messages.add("Invalid sim number");
                            isValid = false;
                        }
                    }
                }

                if (!isValid) faultyRows++;
                rowJson.put("messages", messages);
                rowJson.put("isValid", isValid);
                processedRows.add(rowJson);
            }

        } catch (FileNotFoundException e) {
            log.debug("file missing");
            response.put("error", "File not found");
            return response;
        } catch (IOException e) {
            log.debug("error reading file");
            response.put("error", "Error reading file");
            return response;
        }

        response.put("total Rows", totalRows);
        response.put("valid Rows", totalRows - faultyRows);
        response.put("faulty Rows", faultyRows);
        response.put("null Columns", nullColumns);
        response.put("data", processedRows);
        return response;
    }

}



