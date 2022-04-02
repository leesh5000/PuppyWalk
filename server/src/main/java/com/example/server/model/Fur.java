package com.example.server.model;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "털 종류")
public enum Fur {

    double_coat, // 장모종
    silky_coat, // 견모종
    short_haired_coat, // 단모종
    wire_coat, // 강모종
    curly_coat, // 권모종

}
