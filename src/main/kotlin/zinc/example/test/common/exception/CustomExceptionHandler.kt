package zinc.example.test.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import zinc.example.test.common.dto.BaseResponse
import zinc.example.test.common.status.ResultCode
import java.lang.Exception

@RestControllerAdvice
class CustomExceptionHandler  {

    /** @Valid에서 걸린 경우 발생 **/
    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleValidationExceptions(ex: MethodArgumentNotValidException):
            ResponseEntity<BaseResponse<Map<String, String>>>{
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach{error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }
        return ResponseEntity(BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.msg
        ),HttpStatus.BAD_REQUEST)
    }

    /** 사용자 생성 exception **/
    @ExceptionHandler(InvalidInputException::class)
    protected fun invalidInputException(ex: InvalidInputException):
            ResponseEntity<BaseResponse<Map<String, String>>>{
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.msg
        ),HttpStatus.BAD_REQUEST)
    }

    /** 그 외 exception **/
    @ExceptionHandler(Exception::class)
    protected fun defaultException(ex: Exception):
            ResponseEntity<BaseResponse<Map<String, String>>>{
        val errors = mapOf("미처리 에러" to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.msg
        ),HttpStatus.BAD_REQUEST)
    }
}