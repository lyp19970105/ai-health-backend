package com.example.healthmonitoring.dto.frontend.request;

import com.example.healthmonitoring.dto.common.CommonRequest;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VlmChatRequest extends CommonRequest {

    private MultipartFile image;
    private String text;
    private String appCode;
    private String conversationId;

}
