package org.prokopchuk.facultymcpserver.ingestion.splitter;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.TokenCountEstimator;
import org.apache.commons.lang3.StringUtils;

public class CL100KTokenEstimator implements TokenCountEstimator {

    private final Encoding encoding = Encodings.newDefaultEncodingRegistry()
            .getEncoding(EncodingType.CL100K_BASE);

    @Override
    public int estimateTokenCountInText(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }

        return encoding.countTokens(text);
    }

    @Override
    public int estimateTokenCountInMessage(ChatMessage message) {
        throw new UnsupportedOperationException("Not used for document splitting");
    }

    @Override
    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
        throw new UnsupportedOperationException("Not used for document splitting");
    }

}
