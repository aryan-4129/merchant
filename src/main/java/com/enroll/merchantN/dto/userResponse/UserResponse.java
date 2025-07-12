package com.enroll.merchantN.dto.userResponse;

import lombok.Data;

import java.util.List;

/**
 * @author raghav
 */
@Data
public class UserResponse {
    private String date;
    private String transaction_allowed;
    private String account_status;
    private String user_grade;
    private String type;
    private String bar_type;
    private String txn_id;
    private boolean user_barred;
    private String user_type;
    private String recovery_question_set;
    private String balance;
    private String tpin;
    private String nickname;
    private String pin_set;
    private String id_type;
    private String msisdn;
    private String first_name;
    private String business_name;
    private String id_number;
    private String category_code;
    private String last_name;
    private List<Wallet> wallets;
    private String pin_reset;
    private String additional_data;
    private String nationality;
    private String user_id;
    private String dob;
    private String reg_status;
    private String time;
    private boolean blocked_by_invalid_pin;
    private Parent parent;
}
