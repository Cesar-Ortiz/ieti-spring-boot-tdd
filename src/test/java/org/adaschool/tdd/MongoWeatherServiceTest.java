package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.MongoWeatherService;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class MongoWeatherServiceTest
{
    WeatherService weatherService;

    @Mock
    WeatherReportRepository repository;

    @BeforeEach()
    public void setup() {

        weatherService = new MongoWeatherService( repository );
    }

    @Test
    void weatherReportIdNotFoundTest()
    {
        String weatherReportId = "dsawe1fasdasdoooq123";
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.empty() );
        Assertions.assertThrows( WeatherReportNotFoundException.class, () -> {
            weatherService.findById( weatherReportId );
        } );
    }

    @Test
    void weatherReportIdFoundTest()
    {
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.of( weatherReport ) );
        WeatherReport foundWeatherReport = weatherService.findById( weatherReportId );
        Assertions.assertEquals( weatherReport, foundWeatherReport );
    }

    @Test
    void createWeatherReportCallsSaveOnRepository()
    {
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReportDto weatherReportDto = new WeatherReportDto( location, 35f, 22f, "tester", new Date() );
        weatherService.report( weatherReportDto );
        verify( repository ).save( any( WeatherReport.class ) );
    }

    @Test
    void findNearLocation(){
        List<WeatherReport> reports = new ArrayList<>();
        GeoLocation location1 = new GeoLocation( 0.0, 75.0 );
        GeoLocation location2 = new GeoLocation( 0.0, 75.0 );
        GeoLocation location3 = new GeoLocation( 0.0, 5.0 );
        reports.add(new WeatherReport( location1, 35f, 22f, "tester1", new Date() ));
        reports.add(new WeatherReport( location2, 40f, 12f, "tester2", new Date() ));
        reports.add(new WeatherReport( location3, 12f, 24f, "tester3", new Date() ));
        GeoLocation locationTest = new GeoLocation( 0.0, 75.0 );
        when( repository.findAll()).thenReturn(reports);
        List<WeatherReport> foundNearLocation = weatherService.findNearLocation(locationTest, 1000);
        Assertions.assertEquals( 2, foundNearLocation.size() );
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
        when( repository.findAll()).thenReturn(reports);
        List<WeatherReport> foundWeatherReportsByName = weatherService.findWeatherReportsByName("tester3");
        Assertions.assertEquals( 1, foundWeatherReportsByName.size() );
    }
}
