package com.enroll.merchantN.entity.sqlMerchant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author raghav
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="file_details")
public class FileDetails {

    @Id
    private String id;
    private String path;
    private String clone_path;
    private String file_status;
    private Date created_on;
    private Date modified_on;
    private String created_by;
    private String modified_by;
}
