package com.hubis.acs.common.configuration.customAnnotation;

import com.hubis.acs.common.configuration.protocol.ProtocolCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ProtocolCondition.class)
public @interface EnableProtocol {
    String protocol(); // "tcp", "plc", "opcua"
    boolean server(); // true -> server, false -> client
}
