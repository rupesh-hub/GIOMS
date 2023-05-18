package com.gerp.shared.exception;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.pojo.ApiError;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.utils.ErrorCodeConstants;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
//@Import(DelegatingWebMvcConfiguration.class)
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    // 400
//    apiError
    @Value("${spring.application.name}")
    private String applicationName;
    private final CustomMessageSource customMessageSource;

    public CustomRestExceptionHandler(CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
    }

    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final List<String> errors = new ArrayList<String>();
//        logger.info(new Gson().toJson(ex.getBindingResult()));
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            switch (error.getCode()) {
                case "NotNull":
                    errors.add(customMessageSource.get("notnull", customMessageSource.get(error.getField())));
                    break;
                case "NotBlank":
                    errors.add(customMessageSource.get("notblank", customMessageSource.get(error.getField())));
                    break;
                case "Pattern":
                    errors.add(customMessageSource.get("pattern", customMessageSource.get(error.getField()), error.getRejectedValue()));
                    break;
                default:
                    errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getClass().getName(), errors);
        return handleExceptionInternal(ex, apiError, headers, httpStatus, request);
    }

    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField().toLowerCase();
            String defaultmessage = error.getDefaultMessage();
            Object rejectedValue = error.getRejectedValue();
            switch (error.getCode()) {
                case "NotNull":
                    errors.add(customMessageSource.get("notnull", customMessageSource.get(fieldName)));
                    break;
                case "NotBlank":
                    errors.add(customMessageSource.get("notblank", customMessageSource.get(fieldName)));
                    break;
//                    not working properly
//                case "Min":
//                    errors.add(customMessageSource.get("min", customMessageSource.get(fieldName), error.getArguments()[1]));
//                    break;
//                case "Max":
//                    errors.add(customMessageSource.get("max", customMessageSource.get(fieldName), error.getArguments()[1]));
//                    break;
                case "Size":
                    errors.add(customMessageSource.get("size", customMessageSource.get(fieldName), error.getArguments()[2], error.getArguments()[1]));
                    break;
                case "Pattern":
                    errors.add(customMessageSource.get("pattern", fieldName.toUpperCase(), rejectedValue));
                    break;
                default:
                    errors.add(fieldName + ": " + defaultmessage);
            }
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), errors.get(0), errors);
        return handleExceptionInternal(ex, apiError, headers, httpStatus, request);
    }

    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = customMessageSource.get("error.type.mismatch", ex.getValue(), ex.getPropertyName(), ex.getRequiredType());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = customMessageSource.get("error.request.part.missing", ex.getRequestPartName());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = customMessageSource.get("error.request.parameter.missing", ex.getParameterName());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    //
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = customMessageSource.get("error.method.argument.mismatch", ex.getName(), ex.getRequiredType().getName());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    // 401
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> accessDenied(final AccessDeniedException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        ex.printStackTrace();
        //
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), ex.getLocalizedMessage());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }


    @ExceptionHandler({DuplicateDataException.class})
    @ResponseBody
    public ResponseEntity<Object> duplicateData(final DuplicateDataException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        ex.printStackTrace();
        //
        final String error = customMessageSource.get("error.duplicate.data", ex.getFieldName());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        ex.printStackTrace();
        //
        final List<String> errors = new ArrayList<String>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
//            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), errors.toString(), errors);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({TransactionSystemException.class})
    @ResponseBody
    public ResponseEntity<Object> handleTransactionSystem(final TransactionSystemException e, final WebRequest request) {
        logger.info(e.getClass().getName());
        if (e.getRootCause() instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) e.getRootCause();
            final List<String> errors = new ArrayList<String>();
            for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                if (violation.getMessage().contains("null"))
                    errors.add(customMessageSource.get("error.doesn't.exist", violation.getPropertyPath()));
                else if (violation.getMessage().contains("past"))
                    errors.add(customMessageSource.get("past", customMessageSource.get(violation.getPropertyPath().toString().toLowerCase())));
                else
                    errors.add(violation.getPropertyPath() + " " + violation.getMessage());

            }
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), errors.get(0), errors);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
        } else
            return this.handleAll(e, request);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseBody
    public ResponseEntity<Object> handleConstraintViolation(DataIntegrityViolationException ex, WebRequest request) {
        //ex.printStackTrace();
        final List<String> errors = new ArrayList<String>();
        String fieldName = "";
        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            org.hibernate.exception.ConstraintViolationException violation = ((org.hibernate.exception.ConstraintViolationException) ex.getCause());

            if (violation.getConstraintName().contains("unique_")) {
                String[] datas = violation.getConstraintName().split("_");
                String message = customMessageSource.get(datas[datas.length - 1]);
                errors.add(customMessageSource.get("error.already.exist",
                        message.contains(".") ? customMessageSource.get(message) : message
                ));
            } else if (violation.getConstraintName().contains("_check"))
                errors.add(customMessageSource.get("error.check.constraint", violation.getConstraintName().split("_check")[0]));
            else if (violation.getCause().getLocalizedMessage().contains("not-null"))
                errors.add(customMessageSource.get("error.doesn't.exist", violation.getConstraintName()));
            else if (violation.getCause().getLocalizedMessage().contains("is not present in table"))
                errors.add(customMessageSource.get("error.doesn't.exist", violation.getConstraintName().replace("fk_", "")));
            else if (violation.getCause().getLocalizedMessage().contains("duplicate key value violates unique constraint"))
                errors.add(customMessageSource.get("error.duplicate.data", "Code".replace("fk_", "")));
            else if (violation.getCause().getLocalizedMessage().contains("is still referenced")) {
                String[] constraintName = violation.getConstraintName().split("_");
                fieldName = null;
                try {
                    fieldName = customMessageSource.get(constraintName[0]);
                    errors.add(customMessageSource.get("could.not.delete", fieldName));

                } catch (Exception e) {
                    fieldName = customMessageSource.get("used.in.other.location");
                    errors.add(customMessageSource.get("could.not.delete", fieldName));

                }
                //Matcher matcher = Pattern.compile("fk_(.*?)_[a-zA-Z]+_id").matcher(violation.getConstraintName());
            } else
                errors.add(customMessageSource.get("error.database.error"));
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), errors.get(0), errors);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
        } else if (ex.getCause() instanceof org.hibernate.exception.DataException) {
            org.hibernate.exception.DataException violation = ((org.hibernate.exception.DataException) ex.getCause());
            if (violation.getCause().toString().contains("value too long for type character varying(255)")) {
                fieldName = customMessageSource.get("used.in.other.location");
                errors.add("Text length more than 255 characters");
            }
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), errors.get(0), errors);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
        } else if (ex.getCause() instanceof org.hibernate.StaleObjectStateException) {
            org.hibernate.exception.LockAcquisitionException violation = ((org.hibernate.exception.LockAcquisitionException) ex.getCause());
            if (violation.getCause().toString().contains("Row was updated or deleted by another transaction")) {
                fieldName = customMessageSource.get("used.in.other.location");
                errors.add("Text length more than 255 characters");
            }
        }
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        logger.info(">>>>>>>>>>>>> Error not handled here so , Please contact operator >>>>>>>> ");
        ex.printStackTrace();
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), customMessageSource.get("error.database.error"), ex.getClass().getName());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    // 404
    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    // 405
    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
        HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), builder.toString());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    // 415
    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));
        HttpStatus httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2));
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    //handle runtime exception
    @ExceptionHandler({RuntimeException.class})
    protected ResponseEntity<?> handleRunTimeException(final RuntimeException exception, final WebRequest webRequest) {
        logger.info(exception.getClass().getName());
        logger.error("error", exception);
        exception.printStackTrace();
        return ResponseEntity.ok(GlobalApiResponse.builder().status(ResponseStatus.FAIL).message(exception.getMessage()).build());
    }

    //handle runtime exception
    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<?> handleRunTimeException(final CustomException exception, final WebRequest webRequest) {
        logger.info(exception.getClass().getName());
        logger.error("error", exception);
        exception.printStackTrace();
        return ResponseEntity.ok(GlobalApiResponse.builder().status(ResponseStatus.FAIL).message(exception.getMessage()).build());
    }

    @ExceptionHandler({SQLException.class})
    protected ResponseEntity<?> handleSqlException(final SQLException exception, final WebRequest webRequest) {
        logger.info(exception.getClass().getName());
        logger.error("error occurred on query", exception);
        exception.printStackTrace();
        return ResponseEntity.ok(GlobalApiResponse.builder().status(ResponseStatus.FAIL).message(exception.getMessage()).build());
    }

    @ExceptionHandler({BadSqlGrammarException.class})
    protected ResponseEntity<?> handleSqlException(final BadSqlGrammarException exception, final WebRequest webRequest) {
        logger.info(exception.getClass().getName());
        logger.error("error occurred on query", exception);
        exception.printStackTrace();
        return ResponseEntity.ok(customMessageSource.get("error.system.exception", ErrorCodeConstants.BAD_GRAMMER_EXCEPTION_CODE));
    }

    // 500
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);
        //
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getMessage(), ex.getMessage());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    protected ResponseEntity<Object> handleFileSizeHandler(final MaxUploadSizeExceededException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(customMessageSource.get("file.exceed", "50 MB"));
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), builder.toString(), builder.toString());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class})
    protected ResponseEntity<Object> handleOptimisticLockException(final ObjectOptimisticLockingFailureException ex) {
        logger.info(ex.getClass().getName());
        //
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getLocalizedMessage(), "Mismatched Version Value");
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({ResponseStatusException.class})
    protected ResponseEntity<Object> handleResponseStatusException(final ResponseStatusException ex) {
        logger.info(ex.getClass().getName());

        HttpStatus httpStatus = ex.getStatus();
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getReason(), "");
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({ServiceValidationException.class})
    @ResponseBody
    public ResponseEntity<Object> handleConstraintViolation(final ServiceValidationException ex) {
        logger.info(ex.getClass().getName());
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getMessage(), "");
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({UnAuthenticateException.class})
    @ResponseBody
    public ResponseEntity<Object> handleConstraintViolation(final UnAuthenticateException ex) {
        logger.info(ex.getClass().getName());
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getMessage(), "");
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({RoleException.class})
    @ResponseBody
    public ResponseEntity<Object> handleConstraintViolation(final RoleException ex) {
        logger.info(ex.getClass().getName());
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.TEMPORARY_REDIRECT;
        final ApiError apiError = new ApiError(ResponseStatus.FAIL, httpStatus.value(), ex.getMessage(), "");
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), httpStatus);
    }
}
