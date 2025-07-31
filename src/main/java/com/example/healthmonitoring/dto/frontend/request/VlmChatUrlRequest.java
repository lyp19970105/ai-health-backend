package com.example.healthmonitoring.dto.frontend.request;

import com.example.healthmonitoring.dto.common.CommonRequest;
import lombok.Data;

@Data
public class VlmChatUrlRequest extends CommonRequest {
    private String imageUrl;
    private String text;
    private String appCode;
    private String conversationId;
}
