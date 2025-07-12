package com.enroll.merchantN.entity.mongoMerchant;

import com.enroll.merchantN.dto.MasterDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author raghav
 */
@Data
@Document(collection="merchant_data")
public class MerchantData {
        @Id
        private Long merchant_msisdn;
        private String user_enq_response=null;
        private String bank_enq_response=null;
        private String qr_response=null;
        private MasterDto file_data;

    }
