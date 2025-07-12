package com.enroll.merchantN.entity.mongoMerchant;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author raghav
 */
@Repository
public interface MerchantProfileRepo extends MongoRepository<MerchantProfile, Long> {
}

