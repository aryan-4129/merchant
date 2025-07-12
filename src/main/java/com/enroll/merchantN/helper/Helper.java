package com.enroll.merchantN.helper;

import com.enroll.merchantN.entity.mongoMerchant.MerchantData;
import com.enroll.merchantN.entity.mongoMerchant.MerchantDataRepo;
import com.enroll.merchantN.entity.sqlMerchant.MerchantDetailsRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author raghav
 */
@Service
@Slf4j
public class Helper {

    @Value("${FOLDER_PATH}")
    private String folderPath;
    @Value("${MSISDN_LENGTH:9}")
    int MSISDN_LENGTH;
    @Value("${COUNTRY_CODE}")
    private String COUNTRY_CODE;
    @Value("${EMAIL_REGEX}")
    private static String EMAIL_REGEX="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    @Autowired
    private MerchantDetailsRepo merchantDetailsRepo;
    @Autowired
    private MerchantDataRepo merchantDataRepo;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
    private String targetFilePath;

    public boolean findByMsisdn(String phoneNo){
        //MerchantDetails merchantDetails=merchantDetailsRepo.findByPhoneNo(phoneNo);
//        if(merchantDetails==null) return true;
//        return false;
        return true;
    }
    public String cloneSheet(String path) {

        try (FileInputStream fis = new FileInputStream(new File(path));
             Workbook workbook = new XSSFWorkbook(fis);
             Workbook targetWorkbook = new XSSFWorkbook()) {
            Sheet sourceSheet = workbook.getSheetAt(0);  // Clone first sheet
            Sheet targetSheet = targetWorkbook.createSheet();
            String targetPath=folderPath+sourceSheet.getSheetName()+dateFormat.format(new Date())+".xlsx";
            copySheet(sourceSheet, targetSheet);
            FileOutputStream fos = new FileOutputStream(targetPath);
            workbook.write(fos);
            fos.close();
            modifySheet(targetPath);
            return targetPath;

        } catch (Exception e) {
            log.error("some issue found");

        }
        return null;
    }


    private static void copySheet(Sheet sourceSheet, Sheet targetSheet) {
        for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
            Row sourceRow = sourceSheet.getRow(i);
            Row targetRow = targetSheet.createRow(i);
            if (sourceRow != null) {
                for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                    Cell sourceCell = sourceRow.getCell(j);
                    Cell targetCell = targetRow.createCell(j);
                    if (sourceCell != null) {
                        copyCell(sourceCell, targetCell);
                    }
                }
            }
        }
    }

    private static void copyCell(Cell sourceCell, Cell targetCell) {
        targetCell.setCellType(sourceCell.getCellType());
        switch (sourceCell.getCellType()) {
            case STRING:
                targetCell.setCellValue(sourceCell.getStringCellValue());
                break;
            case NUMERIC:
                targetCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());
                break;
            case BLANK:
                targetCell.setBlank();
                break;
            default:
                log.error("default parsing no similarity found");
                break;
        }
        try {
            Workbook wb = targetCell.getSheet().getWorkbook();
            CellStyle newStyle = wb.createCellStyle();
            newStyle.cloneStyleFrom(sourceCell.getCellStyle());
            targetCell.setCellStyle(newStyle);
        }catch (Exception e){
            log.error("workbook clone error");
        }
    }
    public static int lastemptycell(Row row) {
        int val = -1;
        for (int i = 0; i <= row.getLastCellNum() +1; i++) {
          //  log.info("inside cell iteration: {}",i);
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                //log.info("Value is: {}",val);
                val = i;
                break;
            }
        }
        log.info("returned value: {}",val);
        return val;
    }
    public void modifySheet(String targetFilePath){
        try (FileInputStream fis = new FileInputStream(new File(targetFilePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet1 = workbook.getSheetAt(0);
            String[] newColumn = {"status"};
             int targetRow = 0;
             Row row = sheet1.getRow(targetRow);
             for (int i = 0; i <newColumn.length; i++) {
             int target = lastemptycell(row);
             if (row != null) {
                Cell cell = row.getCell(target);
                if (cell == null) {
                    cell = row.createCell(target);
                }
                cell.setCellValue(newColumn[i]);
            }
        }
            FileOutputStream fos = new FileOutputStream(targetFilePath);
            workbook.write(fos);
            fos.close();
    }

        catch(UnsupportedFileFormatException un){
        log.error("unsupported file");
        }catch(IOException ie){
            log.error("IO Exception");
        }
    }

    public JSONObject validateParameter(String msisdn, String userName, String email){
       JSONObject response=new JSONObject();
        if(!validMsisdn(msisdn) || !validUserName(userName) || !validEmail(email)){
           if(!validMsisdn(msisdn)) {
               response.put("status",false);
               response.put("msisdn", false);
           }
           if(!validUserName(userName)) {
               response.put("status",false);
               response.put("userName", false);
           }
           if(!validEmail(email)) {
               response.put("status",false);
               response.put("email", false);
           }
        }
        return response.put("status",true);
    }
    public boolean validMsisdn(String msisdn) {
        log.info(msisdn);

        if (msisdn == null || msisdn.isEmpty()) {
            log.info("inside null fata");
            return false;
        }
        if (msisdn.length() != MSISDN_LENGTH && msisdn.length()!=12) {
            log.info("inside length fata");
            return false;
        }
        try {
            Long.parseLong(msisdn);
        } catch (Exception e) {
            log.debug("helper class");
            return false;
        }
        return true;
    }
    public boolean validUserName(String userName){
        log.info(userName);
        return userName != null && !userName.isEmpty();
    }
    public boolean validEmail(String email){
        log.info(email);
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
    public boolean validWalletType(String walletType){
        return !walletType.isEmpty() && Constants.WALLET_TYPE.equalsIgnoreCase(walletType);
    }
    public boolean validTillNumber(String tillNumber) {
//        if (5<=tillNumber.length() && 8> tillNumber.length()) {
//            return true;
//        }return false;
            String firstThreeDigits = tillNumber.substring(0, 3);
            try {
                int firstThree = Integer.parseInt(firstThreeDigits);
                return firstThree >= 130 && firstThree <= 149;
            } catch (NumberFormatException e) {
                return false;
            }
    }

    public boolean validPreferredLanguage(String preferredLanguage){
        return Constants.PREFERED_LANGUAGE_1.equalsIgnoreCase(preferredLanguage) ||
                Constants.PREFERED_LANGUAGE_2.equalsIgnoreCase(preferredLanguage);
    }
    public boolean validFeeType(String feeType){
        return Constants.FEE_TYPE.equalsIgnoreCase(feeType);
    }
    public boolean validMerchantType(String merchantType){
        return Constants.MERCHANT_TYPE.equalsIgnoreCase(merchantType);
    }
    public boolean validSimNumber(String simNumber){
        if (simNumber == null || simNumber.isEmpty()) {
            log.info("inside null fata");
            return false;
        }
        if (simNumber.length() != 12 && simNumber.length()!=13) {
            log.info("inside length fata");
            return false;
        }
        if(!simNumber.startsWith(COUNTRY_CODE)) return false;
        try {
            Long.parseLong(simNumber);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public boolean checkInDb(String simNumber){
        log.debug("checking in db");
        MerchantData merchantData=merchantDataRepo.findBySimNumber(simNumber);
        return merchantData != null;
    }
    public boolean checkInDbForWallet(String walletNumber){
        log.info("going to check for wallet no.");
        Long walletNo=Long.parseLong(walletNumber);
        return merchantDataRepo.existsById(walletNo);
    }



}
