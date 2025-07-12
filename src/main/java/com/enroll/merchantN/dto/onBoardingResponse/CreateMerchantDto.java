package com.enroll.merchantN.dto.onBoardingResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author raghav
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMerchantDto {
    private String id;
    private String email;
    private String userName;
    private String phoneNo;
    private String oneTimePassword;
    private boolean passwordResetRequired;
}
