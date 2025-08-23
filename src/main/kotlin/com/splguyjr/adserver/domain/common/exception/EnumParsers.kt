package com.splguyjr.adserver.domain.common.exception

/**
 * 문자열(raw)을 Enum으로 매핑하고,
 * 잘못된 값이 들어오면 커스텀 예외(EnumMappingException)를 던지는 함수.
 *
 * @param raw   매핑하려는 문자열 값
 * @param field 어떤 필드에서 들어온 값인지(에러 메시지에 표시하기 위함)
 * @return 매핑된 Enum 값
 * @throws EnumMappingException 잘못된 문자열일 경우
 */
inline fun <reified E : Enum<E>> enumOrThrow(
    raw: String,
    field: String
): E = try {
    // Java의 Enum.valueOf() 사용 → 정확히 일치하는 enum constant 반환
    java.lang.Enum.valueOf(E::class.java, raw)
} catch (e: IllegalArgumentException) {
    // 매칭 실패 시 커스텀 예외로 래핑해서 던짐
    throw EnumMappingException("Invalid enum for $field: '$raw'")
}

/**
 * 문자열(raw)을 Enum으로 매핑하고,
 * 잘못된 값이 들어오면 null을 반환하는 함수.
 *
 * @param raw   매핑하려는 문자열 값 (nullable 허용)
 * @return 매핑된 Enum 값 또는 null (잘못된 값/입력이 null인 경우)
 */
inline fun <reified E : Enum<E>> enumOrNull(raw: String?): E? =
    try {
        // raw가 null이 아니면 Enum.valueOf로 변환 시도
        raw?.let { java.lang.Enum.valueOf(E::class.java, it) }
    } catch (_: IllegalArgumentException) {
        // 매칭 실패 시 null 반환 (예외 무시)
        null
    }