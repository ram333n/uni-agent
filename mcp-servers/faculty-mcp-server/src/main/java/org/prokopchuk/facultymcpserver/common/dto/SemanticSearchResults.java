package org.prokopchuk.facultymcpserver.common.dto;

import java.util.List;

public record SemanticSearchResults(
    List<SemanticSearchResultEntry> results
) {

}
