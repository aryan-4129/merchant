package com.enroll.merchantN.entity.sqlMerchant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author raghav
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "merchant_details")
public class MerchantDetails {

    @Id
    private long id;
    private String file_id;
    private Date created_on;
    private Date modified_on;
    private String created_by;
    private String modified_by;
}
