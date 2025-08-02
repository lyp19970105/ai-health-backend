package com.example.healthmonitoring.aop;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 日志切面
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 定义切点，拦截所有 Controller 的公共方法
     */
    @Pointcut("execution(public * com.example.healthmonitoring.controller..*.*(..))")
    public void controllerLog() {
    }

    /**
     * 环绕通知
     *
     * @param joinPoint 切点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("controllerLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 准备日志信息
        String url = request.getRequestURL().toString();
        String httpMethod = request.getMethod();
        String ip = request.getRemoteAddr();
        package com.example.healthmonitoring.aop;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Web请求日志切面，用于统一记录所有进入Controller层的方法调用信息。
 * <p>
 * 此切面通过环绕通知（Around Advice）实现，能够在请求处理前、处理后以及异常抛出时记录关键信息。
 * 主要功能包括：
 * 1. 为每个请求生成一个唯一的追踪ID（Trace ID），方便在分布式日志系统中进行端到端追踪。
 * 2. 记录请求的基本信息，如URL、HTTP方法、客户端IP地址。
 * 3. 记录调用的控制器方法及其传入参数（敏感信息如文件、请求/响应对象会被过滤）。
 * 4. 记录方法的执行耗时。
 * 5. 记录方法的正常响应结果或抛出的异常信息。
 * </p>
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 定义切点，此切点匹配com.example.healthmonitoring.controller包及其子包下所有public方法的执行。
     */
    @Pointcut("execution(public * com.example.healthmonitoring.controller..*.*(..))")
    public void controllerLog() {
    }

    /**
     * 对指定的切点（controllerLog）执行环绕通知。
     * <p>
     * 此方法会在目标方法执行前后进行日志记录。它首先记录请求的详细信息，
     * 然后执行目标方法，最后记录方法的执行结果、耗时或异常信息。
     *
     * @param joinPoint 代表被通知的方法（即切点），可以从中获取方法签名、参数等信息，并控制方法的执行。
     * @return 返回目标方法的执行结果。
     * @throws Throwable 如果目标方法执行过程中抛出异常，此通知会捕获、记录日志，然后重新抛出，由全局异常处理器处理。
     */
    @Around("controllerLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 为每个请求生成一个唯一的追踪ID，用于日志链路追踪
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // 获取当前HTTP请求的上下文信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 准备日志所需的基础信息
        String url = request.getRequestURL().toString();
        String httpMethod = request.getMethod();
        String ip = request.getRemoteAddr();
        String controllerMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        // 过滤并序列化请求参数，避免因无法序列化或包含敏感信息导致问题
        Object[] args = joinPoint.getArgs();
        Object[] serializableArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest || args[i] instanceof HttpServletResponse || args[i] instanceof MultipartFile) {
                // 对于无法或不应序列化的特殊类型参数，仅记录其类型
                serializableArgs[i] = args[i].getClass().getSimpleName();
            } else {
                serializableArgs[i] = args[i];
            }
        }
        String params = JSON.toJSONString(serializableArgs);

        log.info("[请求开始] 追踪ID: {} | URL: {} | HTTP方法: {} | IP: {} | 控制器: {} | 参数: {}",
                traceId, url, httpMethod, ip, controllerMethod, params);

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("[请求异常] 追踪ID: {} | 耗时: {}ms | 异常类型: {} | 异常信息: {}",
                    traceId, costTime, e.getClass().getName(), e.getMessage());
            // 异常需要继续向上抛出，以便全局异常处理器能够捕获并进行统一处理
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            String resultJson = (result != null) ? JSON.toJSONString(result) : "null";
            log.info("[请求结束] 追踪ID: {} | 耗时: {}ms | 响应: {}",
                    traceId, costTime, resultJson);
        }
    }
}


        // 过滤并序列化参数
        Object[] args = joinPoint.getArgs();
        Object[] serializableArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest || args[i] instanceof HttpServletResponse || args[i] instanceof MultipartFile) {
                serializableArgs[i] = args[i].getClass().getSimpleName();
            } else {
                serializableArgs[i] = args[i];
            }
        }
        String params = JSON.toJSONString(serializableArgs);

        log.info("[请求开始] 追踪ID: {} | URL: {} | HTTP方法: {} | IP: {} | 控制器: {} | 参数: {}",
                traceId, url, httpMethod, ip, controllerMethod, params);

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("[请求异常] 追踪ID: {} | 耗时: {}ms | 异常类型: {} | 异常信息: {}",
                    traceId, costTime, e.getClass().getName(), e.getMessage());
            // 异常需要继续抛出，由全局异常处理器统一处理
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            String resultJson = (result != null) ? JSON.toJSONString(result) : "null";
            log.info("[请求结束] 追踪ID: {} | 耗时: {}ms | 响应: {}",
                    traceId, costTime, resultJson);
        }
    }
}
