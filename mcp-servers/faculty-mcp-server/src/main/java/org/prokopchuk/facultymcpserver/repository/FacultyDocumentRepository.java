package org.prokopchuk.facultymcpserver.repository;

import org.prokopchuk.facultymcpserver.domain.FacultyDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacultyDocumentRepository extends JpaRepository<FacultyDocument, Long> {

    boolean existsByContentHash(String contentHash);

    @Query("SELECT fd.fileName FROM FacultyDocument fd WHERE fd.id = :documentId")
    String findFileNameById(@Param("documentId") Long documentId);

}
