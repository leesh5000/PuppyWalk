package com.example.server.service;

import com.example.server.dto.WalkIndexResponse;
import com.example.server.model.*;
import com.example.server.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class WalkIndexService {

    private final ObjectMapper om;
    private final AppProperties appProperties;

    public WalkIndexResponse getWalkIndex(Fur fur, Size size, Gender gender, int age, int nx, int ny, Address address) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zdt = now.atZone(ZoneId.of(appProperties.getTimeZone()));
        String baseDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(zdt);
        String baseTime = DateTimeFormatter.ofPattern("HHmm").format(zdt);

        String day = DateTimeFormatter.ofPattern("dd").format(zdt);
        String hour = DateTimeFormatter.ofPattern("HH").format(zdt);
        String minute = DateTimeFormatter.ofPattern("mm").format(zdt);
        if (hour.equals("00") && Integer.parseInt(minute) < 45) { // 00시이고, 아직 45분이 안지났으면 이전 날짜의 23:00시 데이터를 가져와야함
            day = Integer.parseInt(day) - 1 == 0 ? "01" : String.valueOf(Integer.parseInt(day) - 1);
            if (day.length() == 1) {
                day = "0" + day;
            }
            baseDate = DateTimeFormatter.ofPattern("yyyyMM").format(zdt) + day;
            baseTime = "2300";
        } else if (Integer.parseInt(minute) < 45) { // 아직 45분이 안지났으면, 한 시간 이전의 time으로 가져와야함
            hour = Integer.parseInt(hour) - 1 == 0 ? "00" : String.valueOf(Integer.parseInt(hour) - 1);
            baseTime = hour + minute;
        }

        if (baseTime.length() == 3) {
            baseTime = "0" + baseTime;
        }

        log.info("zdt = {}", zdt);
        log.info("baseDate = {}", baseDate);
        log.info("baseTime = {}", baseTime);
        log.info("baseTime = {}", baseTime);

        StringBuilder urlBuilder = new StringBuilder(appProperties.getDustUrl());
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(appProperties.getServiceKey(), StandardCharsets.UTF_8));
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1", StandardCharsets.UTF_8)); /*페이지번호*/
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1000", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
        urlBuilder.append("&").append(URLEncoder.encode("itemCode", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("PM10", StandardCharsets.UTF_8)); /*측정항목 구분(SO2, CO, O3, NO2, PM10, PM2.5)*/
        urlBuilder.append("&").append(URLEncoder.encode("returnType", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("json", StandardCharsets.UTF_8)); /*측정항목 구분(SO2, CO, O3, NO2, PM10, PM2.5)*/
        urlBuilder.append("&").append(URLEncoder.encode("dataGubun", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("HOUR", StandardCharsets.UTF_8)); /*요청 자료 구분(시간평균: HOUR, 일평균: DAILY)*/
        URL dustUrl = new URL(urlBuilder.toString());

        urlBuilder = new StringBuilder(appProperties.getDustUrl());
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(appProperties.getServiceKey(), StandardCharsets.UTF_8));
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1", StandardCharsets.UTF_8)); /*페이지번호*/
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1000", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
        urlBuilder.append("&").append(URLEncoder.encode("itemCode", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("PM25", StandardCharsets.UTF_8)); /*측정항목 구분(SO2, CO, O3, NO2, PM10, PM2.5)*/
        urlBuilder.append("&").append(URLEncoder.encode("returnType", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("json", StandardCharsets.UTF_8)); /*측정항목 구분(SO2, CO, O3, NO2, PM10, PM2.5)*/
        urlBuilder.append("&").append(URLEncoder.encode("dataGubun", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("HOUR", StandardCharsets.UTF_8)); /*요청 자료 구분(시간평균: HOUR, 일평균: DAILY)*/
        URL findDustUrl = new URL(urlBuilder.toString());

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

        List<DustInfo> dustInfos = callApi(dustUrl, ApiType.dust).stream()
                .map(gson::toJson)
                .map(json -> gson.fromJson(json, DustInfo.class))
                .sorted(Comparator.comparing(DustInfo::getDataTime))
                .collect(Collectors.toList())
                .subList(0, 7);

        List<DustInfo> fineDustInfo = callApi(findDustUrl, ApiType.dust).stream()
                .map(gson::toJson)
                .map(json -> gson.fromJson(json, DustInfo.class))
                .sorted(Comparator.comparing(DustInfo::getDataTime))
                .collect(Collectors.toList())
                .subList(0, 7);

        // TODO 오늘 최고온도, 최저온도, 습도 추가해야함
        List<WeatherInfo> weatherInfos = callApi(weatherUrl, ApiType.weather).stream()
                .map(gson::toJson)
                .map(json -> gson.fromJson(json, WeatherInfo.class))
                .collect(Collectors.toList());

        return this.calculate(fur, size, gender, age, address, dustInfos, fineDustInfo, weatherInfos);
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
                log.error("api response error = {}", header.get("resultCode"));
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

    private WalkIndexResponse calculate(Fur fur, Size size, Gender gender, int age, Address address, List<DustInfo> dustInfos, List<DustInfo> fineDustInfos, List<WeatherInfo> weatherInfos) {

        // 총 합이 100점이어야 함
        Map<String, String> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add(weatherInfos.get(0).getFcstTime());
        list.add(weatherInfos.get(0).getFcstTime());
        list.add(weatherInfos.get(1).getFcstTime());
        list.add(weatherInfos.get(2).getFcstTime());
        list.add(weatherInfos.get(3).getFcstTime());
        list.add(weatherInfos.get(4).getFcstTime());
        list.add(weatherInfos.get(5).getFcstTime());

        String currentWalkIndex = "";
        String oneHourWalkIndex = "";
        String twoHourWalkIndex = "";
        String threeHourWalkIndex = "";
        String fourHourWalkIndex = "";
        String fiveHourWalkIndex = "";
        String sixHourWalkIndex = "";

        String currentTemperature = "";
        int highestTemperature = Integer.MIN_VALUE;
        int lowestTemperature = Integer.MAX_VALUE;
        String humidity = "";
        String wind_speed = "";
        String curDust = "";
        String curFineDust = "";
        String weather = "";
        List<String> reasons = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String time = list.get(i);
            List<WeatherInfo> collect = weatherInfos.stream()
                    .filter(weatherInfo -> weatherInfo.getFcstTime().equals(time))
                    .collect(Collectors.toList());
            DustInfo curDustInfo = dustInfos.get(i);
            DustInfo curFineDustInfo = fineDustInfos.get(i);
            String dust = "";
            String fineDust = "";
            switch (address) {
                case daegu:
                    dust = curDustInfo.getDaegu();
                    fineDust = curFineDustInfo.getDaegu();
                    break;
                case chungnam:
                    dust = curDustInfo.getChungnam();
                    fineDust = curFineDustInfo.getChungnam();
                    break;
                case incheon:
                    dust = curDustInfo.getIncheon();
                    fineDust = curFineDustInfo.getIncheon();
                    break;
                case daejeon:
                    dust = curDustInfo.getDaejeon();
                    fineDust = curFineDustInfo.getDaejeon();
                    break;
                case gyeongbuk:
                    dust = curDustInfo.getGyeongbuk();
                    fineDust = curFineDustInfo.getGyeongbuk();
                    break;
                case sejong:
                    dust = curDustInfo.getSejong();
                    fineDust = curFineDustInfo.getSejong();
                    break;
                case gwangju:
                    dust = curDustInfo.getGwangju();
                    fineDust = curFineDustInfo.getGwangju();
                    break;
                case jeonbuk:
                    dust = curDustInfo.getJeonbuk();
                    fineDust = curFineDustInfo.getJeonbuk();
                    break;
                case gangwon:
                    dust = curDustInfo.getGangwon();
                    fineDust = curFineDustInfo.getGangwon();
                    break;
                case ulsan:
                    dust = curDustInfo.getUlsan();
                    fineDust = curFineDustInfo.getUlsan();
                    break;
                case jeonnam:
                    dust = curDustInfo.getJeonnam();
                    fineDust = curFineDustInfo.getJeonnam();
                    break;
                case seoul:
                    dust = curDustInfo.getSeoul();
                    fineDust = curFineDustInfo.getSeoul();
                    break;
                case busan:
                    dust = curDustInfo.getBusan();
                    fineDust = curFineDustInfo.getBusan();
                    break;
                case jeju:
                    dust = curDustInfo.getJeju();
                    fineDust = curFineDustInfo.getJeju();
                    break;
                case chungbuk:
                    dust = curDustInfo.getChungbuk();
                    fineDust = curFineDustInfo.getChungbuk();
                    break;
                case gyeongnam:
                    dust = curDustInfo.getGyeongnam();
                    fineDust = curFineDustInfo.getGyeongnam();
                    break;
                case gyeonggi:
                    dust = curDustInfo.getGyeonggi();
                    fineDust = curFineDustInfo.getGyeonggi();
                    break;
            }
            if (i == 0) {
                curDust = dust;
                curFineDust = fineDust;
            }
            int walkIndex = 100;
            int minus = 0;

            if (Integer.parseInt(dust) < 30) {
                minus += 0;
            } else if (Integer.parseInt(dust) < 80) {
                minus += 5;
            } else if (Integer.parseInt(dust) < 150) {
                minus += 25;
                if (i==0) {
                    reasons.add("미세먼지 나쁨");
                }
            } else {
                minus += 60;
                if (i==0) {
                    reasons.add("미세먼지 매우 나쁨");
                }
            }

            if (Integer.parseInt(fineDust) < 15) {
                minus += 0;
            } else if (Integer.parseInt(fineDust) < 35) {
                minus += 5;
            } else if (Integer.parseInt(fineDust) < 75) {
                minus += 25;
                if (i==0) {
                    reasons.add("초미세먼지 나쁨");
                }
            } else {
                minus += 60;
                if (i==0) {
                    reasons.add("초미세먼지 매우 나쁨");
                }
            }

            for (WeatherInfo weatherInfo : collect) {
                String category = weatherInfo.getCategory();
                if (i == 0) {
                    if (category.equals(Category.REH.toString())) {
                        humidity = weatherInfo.getFcstValue();
                    } else if (category.equals(Category.WSD.toString())) {
                        wind_speed = weatherInfo.getFcstValue();
                    }
                }
                // 온도
                if (category.equals(Category.T1H.toString())) {
                    if (i==0) {
                        currentTemperature = weatherInfo.getFcstValue();
                    }
                    int temperature = Integer.parseInt(weatherInfo.getFcstValue());
                    highestTemperature = Math.max(highestTemperature, temperature);
                    lowestTemperature = Math.min(lowestTemperature, temperature);
                    if (temperature > 40) {
                        minus += 90;
                        if (i==0) {
                            reasons.add("매우 높은 온도");
                        }
                    } else if (35 < temperature) {
                        minus += 46;
                        if (i==0) {
                            reasons.add("높은 온도");
                        }
                    } else if (30 < temperature) {
                        minus += 12;
                    } else if (20 < temperature) {
                        minus += 0;
                    } else if (10 < temperature) {
                        minus += 0;
                    } else if (0 < temperature) {
                        minus += 11;
                        if (i==0) {
                            reasons.add("낮은 온도");
                        }
                    } else if (-5 < temperature) {
                        minus += 22;
                        if (i==0) {
                            reasons.add("매우 낮은 온도");
                        }
                    } else {
                        minus += 52;
                    }
                    // 장모종(double coat)이거나 노견은 더운 날씨에 취약
                    if (temperature >= 35 && (fur.equals(Fur.double_coat) || age > 10)) {
                        minus *= 1.275;
                        if (i == 0) {
                            if (fur.equals(Fur.double_coat)) {
                                reasons.add("장모종은 더운 날씨에 위험해요");
                            }
                            if (age > 10) {
                                reasons.add("10살 이상의 반려도움들은 현재 온도에 위험해요");
                            }
                        }
                    }
                    // 소형견이거나 노견이거나 단모종은 0도 이하에서 매우 위험
                    if (temperature <= 0 && (size.equals(Size.small) || age > 10 || fur.equals(Fur.short_haired_coat))) {
                        minus *= 1.267;
                        if (i == 0) {
                            if (size.equals(Size.small)) {
                                reasons.add("소형견들은 추운 날씨에 위험해요");
                            }
                            if (age > 10) {
                                reasons.add("10살 이상의 반려도움들은 현재 온도에 위험해요");
                            }
                            if (fur.equals(Fur.short_haired_coat)) {
                                reasons.add("단모종은 추운 날씨에 위험해요");
                            }
                        }
                    }
                } else if (category.equals(Category.RN1.toString())) {
                    switch (weatherInfo.getFcstValue()) {
                        case "1.0mm미만":
                            minus += 12;
                            if (i==0) {
                                reasons.add("비가 조금씩 와요");
                            }
                            break;
                        case "1.0~29.0mm":
                            minus += 50;
                            if (i==0) {
                                reasons.add("비가 많이 와요");
                            }
                            break;
                        case "30.0~50.0mm":
                            minus += 100;
                            if (i==0) {
                                reasons.add("비가 매우 많이 와요");
                            }
                            break;
                        default:
                            break;
                    }
                } else if (category.equals(Category.WSD.toString())) {
                    int windSpeed = Integer.parseInt(weatherInfo.getFcstValue());
                    if (windSpeed > 14) {
                        minus += 86;
                        if (i==0) {
                            reasons.add("바람이 매우 많이 불어요");
                        }
                    } else if (windSpeed > 9) {
                        minus += 24;
                        if (i==0) {
                            reasons.add("바람이 많이 불어요");
                        }
                    } else if (windSpeed > 4) {
                        minus += 7;
                        if (i==0) {
                            reasons.add("바람이 조금 불어요");
                        }
                    }
                } else if (category.equals(Category.PTY.toString()) && i == 0) {
                    int rainType = Integer.parseInt(weatherInfo.getFcstValue());
                    if (rainType == 1) {
                        weather = "비";
                    } else if (rainType == 2) {
                        weather = "비/눈";
                    } else if (rainType == 3) {
                        weather = "눈";
                    } else if (rainType == 4) {
                        weather = "소나기";
                    }
                } else if (category.equals(Category.SKY.toString()) && i == 0 && weather.equals("")) {
                    int sky = Integer.parseInt(weatherInfo.getFcstValue());
                    if (sky == 1) {
                        weather = "맑음";
                    } else if (sky == 3) {
                        weather = "구름많음";
                    } else if (sky == 4) {
                        weather = "흐림";
                    }
                }
            }
            walkIndex = Math.max(walkIndex - minus, 0);
            if (i == 0) {
                currentWalkIndex = String.valueOf(walkIndex);
            } else if (i == 1) {
                oneHourWalkIndex = String.valueOf(walkIndex);
            } else if (i == 2) {
                twoHourWalkIndex = String.valueOf(walkIndex);
            } else if (i == 3) {
                threeHourWalkIndex =String.valueOf(walkIndex);
            } else if (i == 4) {
                fourHourWalkIndex = String.valueOf(walkIndex);
            } else if (i == 5) {
                fiveHourWalkIndex = String.valueOf(walkIndex);
            } else if (i == 6) {
                sixHourWalkIndex = String.valueOf(walkIndex);
            }
        }

        return WalkIndexResponse.builder()
                .current_walk_index(currentWalkIndex)
                .one_hours_walk_index(oneHourWalkIndex)
                .two_hours_walk_index(twoHourWalkIndex)
                .three_hours_walk_index(threeHourWalkIndex)
                .four_hours_walk_index(fourHourWalkIndex)
                .five_hours_walk_index(fiveHourWalkIndex)
                .six_hours_walk_index(sixHourWalkIndex)
                .reasons(reasons)
                .info_message(appProperties.getInfoMessage())
                .current_temperature(currentTemperature)
                .highest_temperature(String.valueOf(highestTemperature))
                .lowest_temperature(String.valueOf(lowestTemperature))
                .dust(curDust)
                .fine_dust(curFineDust)
                .air_speed(wind_speed)
                .humidity(humidity)
                .weather(weather)
                .build();
    }
}