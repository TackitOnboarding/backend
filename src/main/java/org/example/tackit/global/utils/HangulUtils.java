package org.example.tackit.global.utils;

public class HangulUtils {
    // 한글 유니코드 시작
    private static final char HANGUL_BEGIN_UNICODE = 0xAC00;
    // 한글 유니코드 끝
    private static final char HANGUL_LAST_UNICODE = 0xD7A3;
    // 한글 초성 리스트
    private static final char[] CHOSUNG = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    // 입력된 문자열에서 한글 초성만 추출
    public static String getChosung(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= HANGUL_BEGIN_UNICODE && c <= HANGUL_LAST_UNICODE) {
                // 유니코드 공식: 초성 index = ((문자코드 - 가) / 28) / 21
                int chosungIndex = (c - HANGUL_BEGIN_UNICODE) / 28 / 21;
                sb.append(CHOSUNG[chosungIndex]);
            } else {
                // 한글이 아니면 그대로 추가
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
