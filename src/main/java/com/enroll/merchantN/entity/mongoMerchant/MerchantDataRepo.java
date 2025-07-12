package com.enroll.merchantN.entity.mongoMerchant;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author raghav
 */
@Repository
public interface MerchantDataRepo extends MongoRepository<MerchantData, Long> {
    @Query("{ 'file_data.simNumber': ?0 }")
    MerchantData findBySimNumber(String simNumber);
    boolean existsById(Long id);
}
