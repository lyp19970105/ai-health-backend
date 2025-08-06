package com.kalby.healthmonitoring.enums;

/**
 * AI 模型类型枚举
 * <p>
 * 用于标识 AI 应用所依赖的底层模型类型，以便前端进行相应的 UI 适配和接口调用。
 *
 * @author C.C.
 * @date 2025/08/03
 */
public enum ModelType {

    /**
     * 纯文本模型 (Text-based Large Language Model)
     * <p>
     * 适用于标准的文本生成、问答、翻译等场景。
     */
    TEXT,

    /**
     * 视觉语言模型 (Vision Language Model)
     * <p>
     * 能够理解和处理图像内容，并结合文本进行问答或分析。
     * 适用于图文对话、图像描述、视觉问答等场景。
     */
    VLM,

}
