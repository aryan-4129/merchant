package com.enroll.merchantN.entity.mongoMerchant;

import com.enroll.merchantN.dto.MasterDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author raghav
 */
@Data
@Document(collection = "merchant_profile")
public class MerchantProfile {
    @Id
    private Long merchant_msisdn;
    private String user_name;
    private String email;
    private String password;
}
