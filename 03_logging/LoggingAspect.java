package com.example.demo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.demo.StreamListenerImpl.onMessage(..))")
    public void logging1() {
        System.out.println("aspect: @Before");
    }

    @AfterReturning("execution(* com.example.demo.StreamListenerImpl.onMessage(..))")
    public void logging2() {
        System.out.println("aspect: @AfterReturning");
    }

    /**
     * aspect: @Around: start
     * aspect: @Before
     * ~~ StreamListenerImpl onMessage ~~
     * aspect: @AfterReturning
     * aspect: @Around: end
     */
    @Around("execution(* com.example.demo.StreamListenerImpl.onMessage(..))")
    public void logging3(ProceedingJoinPoint joinpoint) throws Throwable {
        System.out.println("aspect: @Around: start");
        joinpoint.proceed();
        System.out.println("aspect: @Around: end");
    }
}