package com.example.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WeatherInfo {

    private String baseDate;
    private String baseTime;
    private String category;
    private String fcstDate; // 단기예보 일자
    private String fcstTime; // 단기예보 시간
    private String fcstValue; // 측정값
    private String nx;
    private String ny;

}
