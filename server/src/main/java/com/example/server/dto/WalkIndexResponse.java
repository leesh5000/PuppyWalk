package com.example.server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WalkIndexResponse {

    @ApiModelProperty(example = "현재 산책지수")
    private Integer current_walk_index;
    @ApiModelProperty(example = "2시간 후 산책지수")
    private Integer two_hours_walk_index;
    @ApiModelProperty(example = "4시간 후 산책지수")
    private Integer four_hours_walk_index;
    @ApiModelProperty(example = "종합 산책 점수 도출 참고자료")
    private List<String> references;
    @ApiModelProperty(example = "기온")
    private Integer temperature;
    @ApiModelProperty(example = "댕댕이 체감 온도")
    private Integer sensible_temperature;
    @ApiModelProperty(example = "미세먼지 pm10")
    private Integer dust;
    @ApiModelProperty(example = "초미세먼지 pm2.5")
    private Integer fine_dust;
    @ApiModelProperty(example = "강수량")
    private Integer precipitation;
    @ApiModelProperty(example = "풍속")
    private Integer air_speed;

}
