package com.isp.backend.domain.flight.controller;

import com.amadeus.exceptions.ResponseException;
import com.isp.backend.domain.flight.service.FlightOfferService;
import com.isp.backend.domain.flight.dto.request.FlightSearchRequest;
import com.isp.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings/flights")
@RequiredArgsConstructor
public class FlightOfferController {

    @Autowired
    private FlightOfferService flightOfferService;

    /** 항공권 검색 API **/
    @GetMapping("/search")
    public ResponseEntity<String> getFlightOffers(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                  @RequestBody FlightSearchRequest request) {
        String memberUid = customUserDetails.getUsername();
        try {
            String flightOffersJson = flightOfferService.getFlightOffers(request);
            return ResponseEntity.ok(flightOffersJson);
        } catch (ResponseException e) {
            throw new FlightSearchFailedException();
        }
    }


    /** 항공권 다른 사이트로 연결 API **/


    /** 항공권 좋아요 API **/


    /** 항공권 좋아요 목록 불러오기 API **/


}
