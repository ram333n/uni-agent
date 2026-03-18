package org.prokopchuk.facultymcpserver.common.exception;

public class DuplicateDocumentException extends RuntimeException {

    public DuplicateDocumentException(String contentHash) {
        super("Document with hash " + contentHash + " already exists");
    }

}
