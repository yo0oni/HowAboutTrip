package com.isp.backend.domain.country.controller;

import com.isp.backend.domain.country.dto.request.LocationRequest;
import com.isp.backend.domain.country.dto.response.LocationResponse;
import com.isp.backend.domain.country.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    @Autowired
    private CountryService countryService;

    /** 여행지 좌표 찾기 **/
    @PostMapping("/locations")
    public ResponseEntity<LocationResponse> findLocation(@RequestBody LocationRequest requestDTO) {
        String country = requestDTO.getCountry();
        Optional<LocationResponse> responseDTO = countryService.findLocationByCity(country);
        if (responseDTO.isPresent()) {
            return ResponseEntity.ok(responseDTO.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}