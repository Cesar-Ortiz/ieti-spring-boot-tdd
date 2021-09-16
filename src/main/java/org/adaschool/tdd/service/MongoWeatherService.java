package org.adaschool.tdd.service;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MongoWeatherService implements WeatherService {

    private final WeatherReportRepository repository;

    public MongoWeatherService( @Autowired WeatherReportRepository repository ) {
        this.repository = repository;
    }

    @Override
    public WeatherReport report( WeatherReportDto weatherReportDto )
    {
        WeatherReport report = new WeatherReport(weatherReportDto);
        return repository.save(report);
    }

    @Override
    public WeatherReport findById( String id )
    {
        Optional<WeatherReport> optional = repository.findById( id );
        if ( optional.isPresent() ) {
            return optional.get();
        }
        else {
            throw new WeatherReportNotFoundException();
        }
    }

    @Override
    public List<WeatherReport> findNearLocation( GeoLocation geoLocation, float distanceRangeInMeters )
    {
        List<WeatherReport> reports = repository.findAll();
        List<WeatherReport> reportsFound = new ArrayList<>();
        for(WeatherReport r : reports){
            if(distance(r.getGeoLocation().getLat(),r.getGeoLocation().getLng(),geoLocation.getLat(),geoLocation.getLng()) <= distanceRangeInMeters){
                reportsFound.add(r);
            }
        }
        return reportsFound;
    }

    private double distance(double lat1, double lng1,double lat2, double lng2){
        double earthRadius = 6371;
        double dlon = resta(lng2,lng1);
        double dlat = resta(lat2,lat1);
        double sinlat = Math.sin(dlat / 2);
        double sinlon = Math.sin(dlon / 2);
        double a = (sinlat * sinlat) + Math.cos(lat1)*Math.cos(lat2)*(sinlon*sinlon);
        double c = 2 * Math.asin (Math.min(1.0, Math.sqrt(a)));
        return earthRadius * c * 1000;
    }

    public double resta(double num1, double num2){
        return Math.toRadians(num1-num2);
    }

    @Override
    public List<WeatherReport> findWeatherReportsByName( String reporter ) {
        List<WeatherReport> WeatherReports= repository.findAll();
        List<WeatherReport> WeatherReportsFoundReporter = new ArrayList<>();
        for(WeatherReport r : WeatherReports){
            if(r.getReporter().equals(reporter)){
                WeatherReportsFoundReporter.add(r);
            }
        }
        return WeatherReportsFoundReporter;
    }
}
