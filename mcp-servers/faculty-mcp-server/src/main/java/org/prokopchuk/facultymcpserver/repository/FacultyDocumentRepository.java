package org.prokopchuk.facultymcpserver.repository;

import org.prokopchuk.facultymcpserver.domain.FacultyDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyDocumentRepository extends JpaRepository<FacultyDocument, Long> {

    boolean existsByContentHash(String contentHash);

}
