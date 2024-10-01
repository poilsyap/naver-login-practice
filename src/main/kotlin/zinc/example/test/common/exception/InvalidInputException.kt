package zinc.example.test.common.exception

class InvalidInputException (
    val fieldName: String = "",
    message: String = "Invalid Input"
):RuntimeException(message)