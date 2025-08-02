package com.kalby.healthmonitoring.enums;

/**
 * AI服务平台枚举。
 * <p>
 * 该枚举用于标识系统中集成的不同第三方AI服务提供商。
 * 通过使用此枚举，可以方便地在代码中进行平台切换和特定平台逻辑的处理。
 */
public enum Platform {

    /**
     * 代表 Dify 平台。
     * <p>
     * Dify是一个开源的LLMOps平台，提供了应用编排、API管理等功能。
     */
    DIFY,

    /**
     * 代表 SiliconFlow 平台。
     * <p>
     * SiliconFlow（硅基流动）是一家提供高性能、多模态大模型推理服务的云平台。
     */
    SILICON_FLOW
}