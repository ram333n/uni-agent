package org.prokopchuk.facultymcpserver.service;

import org.springframework.web.multipart.MultipartFile;

public interface FacultyDocumentService {

    Long saveDocument(MultipartFile file);

}
