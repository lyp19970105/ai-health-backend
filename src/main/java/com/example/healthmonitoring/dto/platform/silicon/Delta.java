package com.example.healthmonitoring.dto.platform.silicon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Delta {
    private String content;
}
