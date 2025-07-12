package com.enroll.merchantN.entity.sqlMerchant;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author raghav
 */
public interface FileDetailsRepo extends JpaRepository<FileDetails,String> {

    @Query(value = "select * from file_details where path='UnProcessed'",nativeQuery = true)
    public List<FileDetails> findByStatus();

//    @Query(value="SELECT * FROM file_details  WHERE clone_path IS NOT NULL",nativeQuery = true)
//    Page<FileDetails> findFilesWithClonePath(Pageable pageable);

    @Query(value="SELECT * FROM file_details WHERE created_by=:userId and clone_path IS NOT NULL",nativeQuery = true)
    Page<FileDetails> findFileWithUserId(@Param("userId") String userId,Pageable pageable);

 }
