package com.organizer.contest.remix.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class ExceptionsHandler {
    @ExceptionHandler
    @Order(1)
    public ModelAndView handle(HttpServletRequest req, Exception ex) {
        log.error(req.getMethod() + " request on " + req.getRequestURI() + "caused exception: ", ex);
        ModelAndView modelAndView = new ModelAndView("errors");
        modelAndView.getModel().put("message", ex.getMessage());
        return modelAndView;
    }

}
