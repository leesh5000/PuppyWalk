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
    private Integer nx;
    private Integer ny;
    private String fcstDate;
    private String fcstTime;
    private String fcstValue;

}
