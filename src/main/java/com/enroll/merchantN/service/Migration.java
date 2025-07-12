package com.enroll.merchantN.service;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author raghav
 */
public interface Migration {
   // public JSONObject addFile(FileRequest fileRequest);
    //public FileRequest addCsv(MultipartFile file);
    public JSONObject validateAndSaveCsv(MultipartFile file);
    public List<JSONObject> renderCsv(String fileId);

}
