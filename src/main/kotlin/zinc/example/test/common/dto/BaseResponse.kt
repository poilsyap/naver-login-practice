package zinc.example.test.common.dto

import zinc.example.test.common.status.ResultCode

data class BaseResponse<T> (
        val resultCode: String = ResultCode.SUCCESS.name,
        val data: T? = null,
        val message: String = ResultCode.SUCCESS.msg
)