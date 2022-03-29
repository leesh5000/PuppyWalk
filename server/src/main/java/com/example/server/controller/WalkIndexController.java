package com.example.server.controller;

import com.example.server.dto.WalkIndexResponse;
import com.example.server.service.WalkIndexService;
import com.example.server.util.Address;
import com.example.server.util.Gender;
import com.example.server.util.Size;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class WalkIndexController {

    private final WalkIndexService walkIndexService;

    @GetMapping("/api/walk_index")
    @ApiOperation(value = "산책 지수 조회", notes = "산책 지수 조회 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "size", value = "강아지 크기", required = true),
            @ApiImplicitParam(name = "gender", value = "성별", required = true),
            @ApiImplicitParam(name = "age", value = "나이", required = true),
            @ApiImplicitParam(name = "nx", value = "현재 좌표 x값", dataType = "int", required = true),
            @ApiImplicitParam(name = "ny", value = "현재 좌표 y값", dataType = "int", required = true),
            @ApiImplicitParam(name = "address", value = "현재 주소", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad Request")
    })
    public ResponseEntity<WalkIndexResponse> info(
            @RequestParam("size") Size size,
            @RequestParam("gender") Gender gender,
            @RequestParam("age") Integer age,
            @RequestParam("nx") Integer nx,
            @RequestParam("ny") Integer ny,
            @RequestParam("address") Address address) {

        WalkIndexResponse responseDto = null;
        try {
            responseDto = walkIndexService.getWalkIndex(size, gender, age, nx, ny, address);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(responseDto);
    }

}
