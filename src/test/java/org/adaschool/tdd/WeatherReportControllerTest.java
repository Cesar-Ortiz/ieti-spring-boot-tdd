package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.NearByWeatherReportsQueryDto;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.MongoWeatherService;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class WeatherReportControllerTest {

    @Mock
    WeatherReportRepository repository;

    @MockBean
    WeatherService weatherService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createTest(){
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReportDto weatherReportDto = new WeatherReportDto( location, 35f, 22f, "tester", new Date() );
        this.restTemplate.postForObject("http://localhost:" + port + "/v1/weather", weatherReportDto, WeatherReport.class);
        verify( weatherService ).report( any( WeatherReportDto.class ) );
    }

    @Test
    public void findById() throws Exception {
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when(weatherService.findById( weatherReportId ) ).thenReturn(weatherReport);
        WeatherReport foundWeatherReport2=this.restTemplate.getForObject( "http://localhost:" + port
                + "/v1/weather/{id}", WeatherReport.class, "awae-asd45-1dsad" );
        Assertions.assertEquals( weatherReport.getReporter(), foundWeatherReport2.getReporter());
    }

    @Test
    void findNearByReports(){
        weatherService = new MongoWeatherService( repository );
        List<WeatherReport> reports = new ArrayList<>();
        GeoLocation location1 = new GeoLocation( 0.0, 75.0 );
        GeoLocation location2 = new GeoLocation( 0.0, 75.0 );
        GeoLocation location3 = new GeoLocation( 0.0, 5.0 );
        reports.add(new WeatherReport( location1, 35f, 22f, "tester1", new Date() ));
        reports.add(new WeatherReport( location2, 40f, 12f, "tester2", new Date() ));
        reports.add(new WeatherReport( location3, 12f, 24f, "tester3", new Date() ));
        when( repository.findAll()).thenReturn(reports);
        NearByWeatherReportsQueryDto nearReports = new NearByWeatherReportsQueryDto(new GeoLocation( 0.0, 75.0 ),1000);
        assertThat(this.restTemplate.postForObject( "http://localhost:" + port
                + "/v1/weather/nearby", nearReports, List.class ).size()).isEqualTo(2);
    }

    @Test
    void findWeatherReportsByName(){
        List<WeatherReport> reports = new ArrayList<>();
        GeoLocation location1 = new GeoLocation( 0.0, 75.0 );
        GeoLocation location2 = new GeoLocation( 0.0, 75.0 );
        GeoLocation location3 = new GeoLocation( 0.0, 5.0 );
        reports.add(new WeatherReport( location1, 35f, 22f, "tester3", new Date() ));
        reports.add(new WeatherReport( location2, 40f, 12f, "tester", new Date() ));
        reports.add(new WeatherReport( location3, 12f, 24f, "tester4", new Date() ));
        when( weatherService.findWeatherReportsByName("tester3")).thenReturn(reports);
        //List<WeatherReport> foundWeatherReportsByName = weatherService.findWeatherReportsByName("tester3");
        assertThat(this.restTemplate.getForObject( "http://localhost:" + port
                + "/v1/weather/reporter/{id}", List.class, "tester3" ).size()).isEqualTo(1);
    }
}
