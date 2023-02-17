package com.admin.ozieats_app.utils


enum class Status constructor(val code: Int) {
    SUCCESS(200),
    FAIL(400),
    BAD(502);

    companion object {
        @JvmStatic
        fun getStatusType(numeral: Int?): Status? {
            for (ds in values()) {
                if (ds.code == numeral) {
                    return ds
                }
            }
            return BAD
        }

        @JvmStatic
        fun getStatusTypeInt(type: Status?): Int? {
            return type?.code

        }

        @JvmStatic
        fun getStatusTypeInt(types: List<Status>): List<Int> {
            return types.map { it.code }
        }
    }
}