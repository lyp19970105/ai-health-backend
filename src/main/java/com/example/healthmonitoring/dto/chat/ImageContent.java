package com.example.healthmonitoring.dto.chat;

import lombok.Data;

@Data
public class ImageContent {
    private final String type = "image_url";
    private ImageUrl image_url;

    public ImageContent(ImageUrl image_url) {
        this.image_url = image_url;
    }
}
