package com.enroll.merchantN.helper;

import com.enroll.merchantN.entity.sqlMerchant.FileDetails;
import com.enroll.merchantN.entity.sqlMerchant.FileDetailsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @author raghav
 */
@Slf4j
public class findFilesScheduler {

    @Autowired
    private FileDetailsRepo fileDetailsRepo;
    @Autowired
    private ValidateFile validateFile;
   // @Scheduled(fixedRate = 600000)
    private void findFiles(){
        try {
            List<FileDetails> files = fileDetailsRepo.findByStatus();
            for (FileDetails file : files) {
                validateFile.extractFile(file);
            }
        }catch (IOException ie){
            log.error("error in scheduler");

        }
    }
}
