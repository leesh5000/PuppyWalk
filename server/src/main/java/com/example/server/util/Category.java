package com.example.server.util;

/**
 * 코드 값은 공공데이터 포탈 참고 https://www.data.go.kr/data/15084084/openapi.do
 */
public enum Category {

    T1H, // 기온
    /**
     * 강수량
     * JAVA
     * if(f < 1.0f) return "1.0mm미만 ";
     *  else if(f >= 1.0f && f < 30.0f) return "1.0~29.0mm";
     *  else if(f >= 30.0f && f < 50.0f) return "30.0~50.0mm";
     *  else return "50.0mm이상";
     *  -, null, 0값은 ‘강수없음’
     */
    RN1,
    /**
     * 하늘상태 - 맑음(1), 구름많음(3), 흐림(4)
     */
    SKY,
    /**
     * 동서바람성분 : 동(+표기), 서(-표기)
     */
    UUU,
    /**
     * 남북바람성분(VVV) : 북(+표기), 남(-표기)
     */
    VVV,
    REH, // 습도
    /**
     * 강수형태 (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
     */
    PTY, // 강수형태
    VEC, // 풍향
    WSD, // 풍속

}
