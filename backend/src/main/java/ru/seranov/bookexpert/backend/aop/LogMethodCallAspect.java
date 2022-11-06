package ru.seranov.bookexpert.backend.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

@Aspect
@Component
@Slf4j
public class LogMethodCallAspect {
    @NonNull
    final LoggerChain loggerChain;

    @Autowired
    public LogMethodCallAspect(@NonNull final ObjectMapper mapper) {
        loggerChain = new LoggerChain(mapper, new LoggerRestPost(), new LoggerWebsocketSendTo());
    }

    @Pointcut("within(ru.seranov.bookexpert.backend.controller..*) " +
            "&& @annotation(ru.seranov.bookexpert.backend.aop.LogMethodCall)")
    public void pointcut() {
    }

    interface Logger extends Comparable<Logger> {
        void extendLogData(@NonNull final MethodSignature signature, @NonNull final StringBuilder text);

        @Override
        default int compareTo(Logger o) {
            return Objects.compare(this, o,
                    (o1, o2) -> StringUtils.compare(o1.getClass().getName(), o2.getClass().getName()));
        }
    }

    static class LoggerRestPost implements Logger {
        @Override
        public void extendLogData(final @NonNull MethodSignature signature,
                                  final @NonNull StringBuilder text) {
            final PostMapping mapping = signature.getMethod().getAnnotation(PostMapping.class);
            if (mapping != null) {
                text.append(" path: {").append(Arrays.toString(mapping.path())).append(")}");
            }
        }
    }

    static class LoggerWebsocketSendTo implements Logger {
        @Override
        public void extendLogData(final @NonNull MethodSignature signature,
                                  final @NonNull StringBuilder text) {
            final SendTo sendTo = signature.getMethod().getAnnotation(SendTo.class);
            if (sendTo != null) {
                text.append(" send: {").append(Arrays.toString(sendTo.value())).append(")}");
            }
        }
    }

    static class LoggerChain {
        @NonNull
        private final ObjectMapper mapper;

        private final SortedSet<Logger> loggers = new TreeSet<>();

        LoggerChain(final @NonNull ObjectMapper mapper, @NonNull final Logger... loggers) {
            this.mapper = mapper;
            this.loggers.addAll(List.of(loggers));
        }

        @NonNull
        static MethodSignature getMethodSignature(@NonNull final JoinPoint joinPoint) {
            return (MethodSignature) joinPoint.getSignature();
        }

        @Nullable
        static String toJson(@NonNull final Object object, @NonNull final ObjectMapper mapper) {
            String text;
            try {
                text = mapper.writeValueAsString(object);
            } catch (final JsonProcessingException e) {
                log.error("Error while converting", e);
                text = null;
            }
            return text;
        }

        @Nullable
        static String getParameters(@NonNull final JoinPoint joinPoint, @NonNull final ObjectMapper mapper) {
            final CodeSignature signature = (CodeSignature) joinPoint.getSignature();
            final Map<String, Object> map = new HashMap<>();
            final String[] parameterNames = signature.getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                map.put(parameterNames[i], joinPoint.getArgs()[i]);
            }
            return toJson(map, mapper);
        }

        void log(@NonNull final JoinPoint joinPoint) {
            final MethodSignature signature = getMethodSignature(joinPoint);
            final StringBuilder sb = new StringBuilder();
            sb.append("==> ");
            for (final Logger logger : loggers) {
                logger.extendLogData(signature, sb);
            }
            final String parameters = getParameters(joinPoint, mapper);
            sb.append(" arguments: {").append(parameters).append("}");
            log.info(sb.toString());
        }

        void log(@NonNull final JoinPoint joinPoint, @Nullable final Object entity) {
            final MethodSignature signature = getMethodSignature(joinPoint);
            final StringBuilder sb = new StringBuilder();
            sb.append("<== ");
            for (final Logger logger : loggers) {
                logger.extendLogData(signature, sb);
            }
            final String returning = toJson(entity, mapper);
            sb.append(" returning: {").append(returning).append("}");
            final String parameters = getParameters(joinPoint, mapper);
            sb.append(" arguments: {").append(parameters).append("}");
            log.info(sb.toString());
        }
    }

    @Before("pointcut()")
    public void logMethod(@NonNull final JoinPoint joinPoint) {
        loggerChain.log(joinPoint);
    }

    @AfterReturning(pointcut = "pointcut()", returning = "entity")
    public void logMethodAfter(@NonNull final JoinPoint joinPoint, @NonNull final Object entity) {
        loggerChain.log(joinPoint, entity);
    }
}
