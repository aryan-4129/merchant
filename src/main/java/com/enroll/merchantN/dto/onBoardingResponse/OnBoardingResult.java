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
public class OnBoardingResult {
    private CreateMerchantDto createMerchant;
    private Object groupResponse;
}
