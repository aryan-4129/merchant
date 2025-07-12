package com.enroll.merchantN.dto.userResponse;

import lombok.Data;

/**
 * @author raghav
 */
@Data
public class Wallet {
    private String currency;
    private String wallet_type;
    private String balance;
    private String funds_in_clearance;
    private String frozen;
    private String tcp_id;
    private String total_debit;
    private String total_credit;
    private String is_primary;
    private String till_code;
    private String is_child;
    private String msisdn;
}
