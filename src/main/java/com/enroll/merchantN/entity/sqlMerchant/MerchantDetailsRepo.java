package com.enroll.merchantN.entity.sqlMerchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author raghav
 */
@Repository
public interface MerchantDetailsRepo extends JpaRepository<MerchantDetails, String> {

//    @Query("{ 'merchantMsisdn': ?0 }")
//    MerchantDetails findByPhoneNo(String phoneNo);
}