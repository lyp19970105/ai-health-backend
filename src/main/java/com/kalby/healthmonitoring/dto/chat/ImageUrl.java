package com.kalby.healthmonitoring.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片URL数据DTO。
 * <p>
 * 该对象用于封装图片的具体URL地址，通常是Base64编码的Data URI。
 * 它是 {@link ImageContent} 的一部分，用于向模型传递图像数据。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUrl {

    /**
     * 图片的URL。
     * <p>
     * 通常采用Base64编码的Data URI格式 (e.g., "data:image/jpeg;base64,...")，
     * 以便将图片数据直接嵌入到JSON请求中。
     */
    private String url;

    /**
     * 图像的细节级别（可选）。
     * <p>
     * 用于控制模型如何处理和理解图像。某些模型支持此参数。
     * - "low": 低分辨率模式，可能会更快，成本更低。
     * - "high": 高分辨率模式，模型将更详细地查看图像。
     * - "auto" (或不提供): 由模型决定。
     * 默认为 "auto"。
     */
    private String detail = "auto";

    /**
     * 便捷构造函数，仅需传入URL。
     * detail字段将使用默认值 "auto"。
     *
     * @param url 图片的URL (通常是Data URI)。
     */
    public ImageUrl(String url) {
        this.url = url;
    }
}