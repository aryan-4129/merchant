package com.enroll.merchantN.helper;


import com.enroll.merchantN.dto.MasterDto;
import com.enroll.merchantN.entity.mongoMerchant.MerchantData;
import com.enroll.merchantN.entity.mongoMerchant.MerchantDataRepo;
import com.enroll.merchantN.entity.mongoMerchant.MerchantProfile;
import com.enroll.merchantN.entity.mongoMerchant.MerchantProfileRepo;
import com.enroll.merchantN.entity.sqlMerchant.FileDetails;
import com.enroll.merchantN.entity.sqlMerchant.FileDetailsRepo;
import com.enroll.merchantN.entity.sqlMerchant.MerchantDetails;
import com.enroll.merchantN.entity.sqlMerchant.MerchantDetailsRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;


/**
 * @author raghav
 */
@Slf4j
@Service
public class ValidateFile {
    @Autowired
    private MerchantDetailsRepo miniMerchantRepo;
    @Autowired
    private MerchantDataRepo mongoRepo;
    @Autowired
    private KafkaSender sender;
    @Autowired
    private FileDetailsRepo fileDetailsRepo;
    @Autowired
    private Helper helper;
    @Autowired
    private MerchantProfileRepo merchantProfileRepo;
    @Autowired
    private ObjectMapper mapper;

    private static LinkedHashMap<String, Integer> hash = new LinkedHashMap<>();


    public void extractFile(FileDetails fileRequest) throws IOException {
        FileInputStream fis = new FileInputStream(new File(fileRequest.getPath()));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        String clonedSheetName = helper.cloneSheet(fileRequest.getPath());
        log.info("Processing sheet: " + sheet.getSheetName());
        Row row = sheet.getRow(0);
        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (!isCellEmpty(cell)) {
                hash.put(cell.getStringCellValue(), i);
            }
        }
        for (int j = sheet.getFirstRowNum() + 1; j <= sheet.getPhysicalNumberOfRows(); j++) {
            Row row1 = sheet.getRow(j);
            if (row1 == null || isRowEmpty(row1)) {
                break;
            } else createMasterDto(row1, fileRequest, clonedSheetName);
        }


    }


    public static boolean isCellEmpty(Cell cell) {
        log.debug("is cell empty");
        if (cell == null) return true;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim().isEmpty();
            case BLANK:
                return true;
            case NUMERIC:
            case BOOLEAN:
            case FORMULA:
                return false;
            default:
                return true;
        }
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private void createMasterDto(Row row1, FileDetails fileDetails, String clonedSheet) throws JsonProcessingException {
        MasterDto merchantDTO = new MasterDto();
        try {
            for (String s : hash.keySet()) {
                switch (s) {
                    case "Onboarding":
                        merchantDTO.setOnboarding(getStringCellValue(row1, s));
                        break;
                    case "MCC Code":
                        merchantDTO.setMccCode(getStringCellValue(row1, s));
                        break;
                    case "Type":
                        merchantDTO.setType(getStringCellValue(row1, s));
                        break;
                    case "Tier":
                        merchantDTO.setTier(getStringCellValue(row1, s));
                        break;
                    case "Risk Grade":
                        merchantDTO.setRiskGrade(getStringCellValue(row1, s));
                        break;
                    case "Business (location)":
                        merchantDTO.setBusinessLocation(getStringCellValue(row1, s));
                        break;
                    case "Continent":
                        merchantDTO.setContinent(getStringCellValue(row1, s));
                        break;
                    case "Region":
                        merchantDTO.setRegion(getStringCellValue(row1, s));
                        break;
                    case "Country":
                        merchantDTO.setCountry(getStringCellValue(row1, s));
                        break;
                    case "City":
                        merchantDTO.setCity(getStringCellValue(row1, s));
                        break;
                    case "District":
                        merchantDTO.setDistrict(getStringCellValue(row1, s));
                        break;
                    case "Street":
                        merchantDTO.setStreet(getStringCellValue(row1, s));
                        break;
                    case "Ward":
                        merchantDTO.setWard(getStringCellValue(row1, s));
                        break;
                    case "Plot":
                        merchantDTO.setPlot(getStringCellValue(row1, s));
                        break;
                    case "Address":
                        merchantDTO.setAddress(getStringCellValue(row1, s));
                        break;
                    case "Landmark":
                        merchantDTO.setLandmark(getStringCellValue(row1, s));
                        break;
                    case "Location (PIN)":
                        merchantDTO.setLocationPin(getStringCellValue(row1, s));
                        break;
                    case "Coordinates":
                        merchantDTO.setCoordinates(getStringCellValue(row1, s));
                        break;
                    case "Postal code":
                        merchantDTO.setPostalCode(getStringCellValue(row1, s));
                        break;
                    case "PO Box":
                        merchantDTO.setPoBox(getStringCellValue(row1, s));
                        break;
                    case "Issuer":
                        merchantDTO.setIssuer(getStringCellValue(row1, s));
                        break;
                    case "Acquirer":
                        merchantDTO.setAcquirer(getStringCellValue(row1, s));
                        break;
                    case "FSP":
                        merchantDTO.setFsp(getStringCellValue(row1, s));
                        break;
                    case "Gateway":
                        merchantDTO.setGateway(getStringCellValue(row1, s));
                        break;
                    case "Aggregator":
                        merchantDTO.setAggregator(getStringCellValue(row1, s));
                        break;
                    case "Sub-Aggregator":
                        merchantDTO.setSubAggregator(getStringCellValue(row1, s));
                        break;
                    case "Merchant Segment":
                        merchantDTO.setMerchantSegment(getStringCellValue(row1, s));
                        break;
                    case "Wallet":
                        merchantDTO.setWallet(getStringCellValue(row1, s));
                        break;
                    case "Wallet Type":
                        merchantDTO.setWalletType(getStringCellValue(row1, s));
                        break;
                    case "Hierarchy":
                        merchantDTO.setHierarchy(getStringCellValue(row1, s));
                        break;
                    case "Role":
                        merchantDTO.setRole(getStringCellValue(row1, s));
                        break;
                    case "Access privilege":
                        merchantDTO.setAccessPrivilege(getStringCellValue(row1, s));
                        break;
                    case "Merchant Type":
                        merchantDTO.setMerchantType(getStringCellValue(row1, s));
                        break;
                    case "USER_NAME_PREFIX":
                        merchantDTO.setUserNamePrefix(getStringCellValue(row1, s));
                        break;
                    case "Merchant Name":
                        merchantDTO.setMerchantName(getStringCellValue(row1, s));
                        break;
                    case "Representative Name":
                        merchantDTO.setRepresentativeName(getStringCellValue(row1, s));
                        break;
                    case "Email":
                        merchantDTO.setEmail(getStringCellValue(row1, s));
                        break;
                    case "SIM number":
                        merchantDTO.setSimNumber(getStringCellValue(row1, s));
                        break;
                    case "Wallet Num (MSISDN)":
                        merchantDTO.setWalletNumMsisdn(getStringCellValue(row1, s));
                        break;
                    case "TILL Number":
                        merchantDTO.setTillNumber(getStringCellValue(row1, s));
                        break;
                    case "Hierarchy Type":
                        merchantDTO.setHierarchyType(getStringCellValue(row1, s));
                        break;
                    case "Hierarchy Level":
                        merchantDTO.setHierarchyLevel(getStringCellValue(row1, s));
                        break;
                    case "Location PIN":
                        merchantDTO.setLocationPinSecondary(getStringCellValue(row1, s));
                        break;
                    case "Website":
                        merchantDTO.setWebsite(getStringCellValue(row1, s));
                        break;
                    case "Whatsapp":
                        merchantDTO.setWhatsapp(getStringCellValue(row1, s));
                        break;
                    case "Telegram":
                        merchantDTO.setTelegram(getStringCellValue(row1, s));
                        break;
                    case "Instagram":
                        merchantDTO.setInstagram(getStringCellValue(row1, s));
                        break;
                    case "Facebook":
                        merchantDTO.setFacebook(getStringCellValue(row1, s));
                        break;
                    case "X (Twitter)":
                        merchantDTO.setTwitter(getStringCellValue(row1, s));
                        break;
                    case "TikTok":
                        merchantDTO.setTiktok(getStringCellValue(row1, s));
                        break;
                    case "Logo":
                        merchantDTO.setLogo(getStringCellValue(row1, s));
                        break;
                    case "Prefered Language":
                        merchantDTO.setPreferredLanguage(getStringCellValue(row1, s));
                        break;
                    case "Business Name (Display)":
                        merchantDTO.setBusinessNameDisplay(getStringCellValue(row1, s));
                        break;
                    case "Trading Name":
                        merchantDTO.setTradingName(getStringCellValue(row1, s));
                        break;
                    case "ID Type":
                        merchantDTO.setIdType(getStringCellValue(row1, s));
                        break;
                    case "TIN":
                        merchantDTO.setTin(getStringCellValue(row1, s));
                        break;
                    case "VRN":
                        merchantDTO.setVrn(getStringCellValue(row1, s));
                        break;
                    case "Bank Type":
                        merchantDTO.setBankType(getStringCellValue(row1, s));
                        break;
                    case "Bank Country":
                        merchantDTO.setBankCountry(getStringCellValue(row1, s));
                        break;
                    case "SWIFT":
                        merchantDTO.setSwift(getStringCellValue(row1, s));
                        break;
                    case "IBAN":
                        merchantDTO.setIban(getStringCellValue(row1, s));
                        break;
                    case "NETWORK_CODE":
                        merchantDTO.setNetworkCode(getStringCellValue(row1, s));
                        break;
                    case "Auto Sweep Allowed":
                        merchantDTO.setAutoSweepAllowed(getStringCellValue(row1, s));
                        break;
                    default:
                        break;
                }

            }
        } catch (Exception e) {

        }
        try{
        if (!merchantDTO.getWalletNumMsisdn().isEmpty() && helper.findByMsisdn(String.valueOf(merchantDTO.getContactDetails()))
            && !merchantDTO.getEmail().isEmpty()) {

                String mobileNo = "";
                //String simNumber = validate(merchantDTO.getSimNumber());
            String simNumber=merchantDTO.getSimNumber();
                if (merchantDTO.getWalletNumMsisdn().length() == 12)
                    mobileNo = merchantDTO.getWalletNumMsisdn().substring(3, 12);
                else mobileNo = merchantDTO.getWalletNumMsisdn();
                MerchantData merchantMongoData = new MerchantData();
                MerchantProfile merchantProfile = new MerchantProfile();
                log.info("WALLET NUMBER: " + mobileNo);
                log.info("SIM NUMBER: " + simNumber);
                fileDetails.setFile_status("inProcess");
                fileDetails.setClone_path(clonedSheet);
                MerchantDetails miniMerchant = new MerchantDetails();
                miniMerchant.setId(Long.parseLong(mobileNo));
                miniMerchant.setFile_id(fileDetails.getId());
                miniMerchantRepo.save(miniMerchant);
                log.info("file saved successfully in sql");
                merchantMongoData.setMerchant_msisdn(Long.valueOf(mobileNo));
                merchantProfile.setMerchant_msisdn(Long.valueOf(simNumber));
                merchantProfile.setUser_name(merchantDTO.getMerchantName());

                merchantProfile.setEmail(merchantDTO.getEmail());
//            String sanitizedDto=sanitizeAll(merchantDTO.toString());
//            log.info("sanitized dto: {}",sanitizedDto);
//            MasterDto msDto=mapper.readValue(sanitizedDto,MasterDto.class);
                merchantMongoData.setFile_data(merchantDTO);
//            merchantMongoData.setFile_data(msDto);
                merchantProfileRepo.save(merchantProfile);
                mongoRepo.save(merchantMongoData);
                log.info("file saved successfully in mongo");
                sender.sendMessage(mobileNo);
            } else{
                fileDetails.setFile_status(
                        "failed");
                log.error("null value found data cannot be saved");

            }
            fileDetailsRepo.save(fileDetails);
        }catch(Exception e){
            log.error("unable to save data");

        }
    }

    private String getStringCellValue(Row row, String columnName) {
        int columnIndex = getColumnIndex(columnName);
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Column '" + columnName + "' not found in Excel header");
        }
        Cell cell=row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()); // Convert to integer if needed
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    private int getColumnIndex(String columnName) {
        return hash.getOrDefault(columnName, -1);
    }


    // Helper method for boolean values
    private boolean getBooleanCellValue(Row row, String key) {
        return !isCellEmpty(row.getCell(hash.get(key))) && row.getCell(hash.get(key)).getBooleanCellValue();
    }
    private String validate(String number){
        log.info("entering sim number");
        if(number.length()==12) {
            return number.substring(3,12);
        }else{
            return number;
        }
    }


}
