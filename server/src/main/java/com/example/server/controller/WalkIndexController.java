package com.example.server.controller;

import com.example.server.dto.WalkIndexResponse;
import com.example.server.model.Fur;
import com.example.server.service.WalkIndexService;
import com.example.server.model.Address;
import com.example.server.model.Gender;
import com.example.server.util.LatLngToXY;
import com.example.server.util.Size;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class WalkIndexController {

    private final WalkIndexService walkIndexService;

    @GetMapping("/api/walk_index")
    @ApiOperation(value = "산책 지수 조회", notes = "산책 지수 조회 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fur", value = "털 종류", required = true),
            @ApiImplicitParam(name = "size", value = "강아지 크기", required = true),
            @ApiImplicitParam(name = "gender", value = "성별", required = true),
            @ApiImplicitParam(name = "age", value = "나이", dataType = "int", required = true),
            @ApiImplicitParam(name = "lat", value = "위도", dataType = "float", required = true),
            @ApiImplicitParam(name = "lng", value = "경도", dataType = "float", required = true),
            @ApiImplicitParam(name = "address", value = "현재 주소", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    public ResponseEntity<WalkIndexResponse> info(
            @RequestParam("fur") Fur fur,
            @RequestParam("size") Size size,
            @RequestParam("gender") Gender gender,
            @RequestParam("age") Integer age,
            @RequestParam("lat") float lat,
            @RequestParam("lng") float lng,
            @RequestParam("address") Address address) {

        Map<String, Integer> map = LatLngToXY.transToXY(lat, lng);
        WalkIndexResponse responseDto = null;
        try {
            responseDto = walkIndexService.getWalkIndex(fur, size, gender, age, map.get("nx"), map.get("ny"), address);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(responseDto);
    }

}
