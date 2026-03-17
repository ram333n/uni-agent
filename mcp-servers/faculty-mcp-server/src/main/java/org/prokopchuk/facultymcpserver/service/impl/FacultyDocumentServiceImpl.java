package org.prokopchuk.facultymcpserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacultyDocumentServiceImpl implements FacultyDocumentService {

    @Override
    public Long saveDocument(String content) {
        return 0L;
    }

}
