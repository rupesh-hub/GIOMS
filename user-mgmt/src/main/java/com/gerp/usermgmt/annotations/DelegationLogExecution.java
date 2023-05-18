package com.gerp.usermgmt.annotations;

import com.gerp.usermgmt.enums.DelegationLogEnum;
import com.gerp.usermgmt.model.delegation.DelegationLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DelegationLogExecution {
}
