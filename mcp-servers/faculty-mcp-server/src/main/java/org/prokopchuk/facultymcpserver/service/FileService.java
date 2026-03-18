package org.prokopchuk.facultymcpserver.service;

import java.nio.file.Path;

public interface FileService {

    String computeSha256Hex(byte[] data);

    Path persistToDisk(String fileName, byte[] fileBytes);

}
