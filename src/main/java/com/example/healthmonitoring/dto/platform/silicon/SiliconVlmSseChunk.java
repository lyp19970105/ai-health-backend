package com.example.healthmonitoring.dto.platform.silicon;

import com.example.healthmonitoring.dto.platform.dify.DifySseEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiliconVlmSseChunk {
    private String id;
    private List<Choice> choices;
    private DifySseEvent.Usage usage;
}
