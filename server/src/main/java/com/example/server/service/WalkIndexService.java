package com.example.server.service;

import com.example.server.dto.WalkIndexResponse;
import com.example.server.model.DustInfo;
import com.example.server.model.WeatherInfo;
import com.example.server.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WalkIndexService {

    private final ObjectMapper om;
    private final AppProperties appProperties;

    public WalkIndexResponse getWalkIndex(Size size, Gender gender, int age, int nx, int ny, Address address) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zdt = now.atZone(ZoneId.of(appProperties.getTimeZone()));
        String baseDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(zdt);

//        int hour = Integer.parseInt(DateTimeFormatter.ofPattern("HH").format(zdt));
//        int minutes = Integer.parseInt(DateTimeFormatter.ofPattern("mm").format(zdt));
//        String baseTime = (hour - 1) < 0 ? "2300" : (hour - 1) + "00";
//        if (minutes > 45) { // Open API 데이터 제공 시간 이후이면,
//            baseTime = DateTimeFormatter.ofPattern("HHmm").format(zdt);
//        }

        String baseTime = DateTimeFormatter.ofPattern("HHmm").format(zdt);
        if (baseTime.length() == 3) {
            baseTime = "0" + baseTime;
        }

        StringBuilder urlBuilder = new StringBuilder(appProperties.getDustUrl());
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(appProperties.getServiceKey(), StandardCharsets.UTF_8));
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1", StandardCharsets.UTF_8)); /*페이지번호*/
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1000", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
        urlBuilder.append("&").append(URLEncoder.encode("itemCode", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("PM10", StandardCharsets.UTF_8)); /*측정항목 구분(SO2, CO, O3, NO2, PM10, PM2.5)*/
        urlBuilder.append("&").append(URLEncoder.encode("returnType", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("json", StandardCharsets.UTF_8)); /*측정항목 구분(SO2, CO, O3, NO2, PM10, PM2.5)*/
        urlBuilder.append("&").append(URLEncoder.encode("dataGubun", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("HOUR", StandardCharsets.UTF_8)); /*요청 자료 구분(시간평균: HOUR, 일평균: DAILY)*/
        URL dustUrl = new URL(urlBuilder.toString());

        urlBuilder = new StringBuilder(appProperties.getWeatherUrl());
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(appProperties.getServiceKey(), StandardCharsets.UTF_8));
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1", StandardCharsets.UTF_8)); /*페이지번호*/
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1000", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
        urlBuilder.append("&").append(URLEncoder.encode("dataType", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("json", StandardCharsets.UTF_8)); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&").append(URLEncoder.encode("base_date", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(baseDate, StandardCharsets.UTF_8)); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&").append(URLEncoder.encode("base_time", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(baseTime, StandardCharsets.UTF_8)); /*06시 발표(정시단위) */
        urlBuilder.append("&").append(URLEncoder.encode("nx", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(String.valueOf(nx), StandardCharsets.UTF_8)); /*예보지점의 X 좌표값*/
        urlBuilder.append("&").append(URLEncoder.encode("ny", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(String.valueOf(ny), StandardCharsets.UTF_8)); /*예보지점의 Y 좌표값*/
        URL weatherUrl = new URL(urlBuilder.toString());

        Gson gson = new Gson();

        String curDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00").format(zdt);
        List<DustInfo> dustInfos = callApi(dustUrl, ApiType.dust).stream()
                .map(gson::toJson)
                .map(json -> gson.fromJson(json, DustInfo.class))
                .filter(dustInfo -> dustInfo.getDataTime().equals(curDate))
                .collect(Collectors.toList());

        List<WeatherInfo> weatherInfos = callApi(weatherUrl, ApiType.weather).stream()
                .map(gson::toJson)
                .map(json -> gson.fromJson(json, WeatherInfo.class))
                .collect(Collectors.toList());

        WalkIndexResponse calculate = this.calculate(size, gender, age, dustInfos, weatherInfos);

        return null;
    }

    private List<Object> callApi(URL url, ApiType apiType) throws Exception {

        HttpURLConnection conn = null;
        BufferedReader br = null;
        HashMap<String, Object> result;
        ArrayList<Object> infos = null;

        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Content-type", "application/json");
            conn.setDoOutput(true);

            conn.setRequestMethod("GET");
            conn.connect();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String output;
            StringBuilder sb = new StringBuilder();

            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            result = (HashMap<String, Object>) om.readValue(sb.toString(), Map.class).get("response");

            Map header = (HashMap) result.get("header");
            Map body = (HashMap) result.get("body");
            if (!header.get("resultCode").equals("00")) {

            } else {
                if (apiType.equals(ApiType.dust)) {
                    infos = (ArrayList<Object>) body.get("items");
                } else if (apiType.equals(ApiType.weather)) {
                    Map items = (HashMap) body.get("items");
                    infos = (ArrayList<Object>) items.get("item");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (br != null) {
                br.close();
            }
        }

        return infos;
    }

    private WalkIndexResponse calculate(Size size, Gender gender, int age, List<DustInfo> dustInfos, List<WeatherInfo> weatherInfos) {

        // 총 합이 100점이어야 함

        //

        return null;
    }
}