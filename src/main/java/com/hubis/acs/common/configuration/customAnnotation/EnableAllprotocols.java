package com.hubis.acs.common.configuration.customAnnotation;

import com.hubis.acs.common.configuration.protocol.ProtocolAutoImportConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ProtocolAutoImportConfig.class)
public @interface EnableAllprotocols {}
