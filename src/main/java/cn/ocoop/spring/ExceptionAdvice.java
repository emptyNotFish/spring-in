package cn.ocoop.spring;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

@RestControllerAdvice
public class ExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);
    @Autowired
    Environment environment;

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Map<String, Object> handleBindException(HttpServletRequest request, BindException bindException) throws IOException {
        return errorProcess(request, "请求参数格式错误", buildObjectBindException(bindException.getBindingResult()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleBindException(HttpServletRequest request, MethodArgumentNotValidException bindException) throws IOException {
        return errorProcess(request, "请求参数格式错误", buildObjectBindException(bindException.getBindingResult()));
    }

    private Exception buildObjectBindException(BindingResult bindingResult) {
        return new InvalidParameterException(
                bindingResult.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getObjectName() + "." + fieldError.getField() + ":" + fieldError.getDefaultMessage() + ";")
                        .reduce(String::concat).get()
        );
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> handleBindException(HttpServletRequest request, ConstraintViolationException bindException) throws IOException {
        return errorProcess(request, "请求参数格式错误", new InvalidParameterException(
                bindException.getConstraintViolations()
                        .stream()
                        .map(constraintViolation -> constraintViolation.getMessage() + ";")
                        .reduce(String::concat).get()
        ));
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws IOException {
        return errorProcess(request, "系统异常", ex);
    }

    private Map<String, Object> errorProcess(HttpServletRequest request, String exceptionSubject, Throwable ex) throws IOException {
        log.error(exceptionSubject + ",{}", request.getRequestURL() + "?" + request.getQueryString(), ex);
        Map<String, Object> errorMsg = Maps.newHashMap();
        errorMsg.put(environment.getProperty("app.error.title", "sysErrorTitle"), exceptionSubject);
        errorMsg.put(environment.getProperty("app.error.class", "sysErrorClass"), ClassUtils.getShortName(ex.getClass()));
        errorMsg.put(environment.getProperty("app.error.msg", "sysErrorMsg"), ex.getMessage());
        return errorMsg;
    }

}
