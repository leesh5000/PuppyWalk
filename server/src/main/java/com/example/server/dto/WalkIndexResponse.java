package com.example.server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WalkIndexResponse {

    @ApiModelProperty(example = "현재 산책지수", position = 0)
    private String current_walk_index;
    @ApiModelProperty(example = "1시간 후 산책지수", position = 1)
    private String one_hours_walk_index;
    @ApiModelProperty(example = "2시간 후 산책지수", position = 2)
    private String two_hours_walk_index;
    @ApiModelProperty(example = "3시간 후 산책지수", position = 3)
    private String three_hours_walk_index;
    @ApiModelProperty(example = "4시간 후 산책지수", position = 4)
    private String four_hours_walk_index;
    @ApiModelProperty(example = "5시간 후 산책지수", position = 5)
    private String five_hours_walk_index;
    @ApiModelProperty(example = "6시간 후 산책지수", position = 6)
    private String six_hours_walk_index;
    @ApiModelProperty(example = "산책 점수 이유", position = 7)
    private List<String> reasons;
    @ApiModelProperty(example = "안내문", position = 8)
    private String info_message;
    @ApiModelProperty(example = "현재 기온 (C)", position = 9)
    private String current_temperature;
    @ApiModelProperty(example = "오늘 최고 기온 (C)", position = 10)
    private String highest_temperature;
    @ApiModelProperty(example = "오늘 최저 기온 (C)", position = 11)
    private String lowest_temperature;
    @ApiModelProperty(example = "미세먼지 (pm10)", position = 12)
    private String dust;
    @ApiModelProperty(example = "초미세먼지 (pm2.5)", position = 13)
    private String fine_dust;
    @ApiModelProperty(example = "풍속 (m/s)", position = 14)
    private String air_speed;
    @ApiModelProperty(example = "습도(%)", position = 15)
    private String humidity;
    @ApiModelProperty(example = "날씨상태", position = 16, allowableValues = "맑음, 구름많음, 흐림, 비, 비/눈, 눈, 소나기")
    private String weather;

}
