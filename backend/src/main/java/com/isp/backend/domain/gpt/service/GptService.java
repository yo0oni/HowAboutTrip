package com.isp.backend.domain.gpt.service;

import com.isp.backend.domain.gpt.config.GptConfig;
import com.isp.backend.domain.gpt.constant.ParsingConstants;
import com.isp.backend.domain.gpt.dto.GptRequestDTO;
import com.isp.backend.domain.gpt.dto.GptResponseDTO;
import com.isp.backend.domain.gpt.dto.GptScheduleRequestDto;
import com.isp.backend.domain.gpt.dto.GptScheduleResponseDto;
import com.isp.backend.domain.gpt.entity.GptMessage;
import com.isp.backend.domain.gpt.entity.GptSchedule;
import com.isp.backend.domain.gpt.entity.GptScheduleParser;
import com.isp.backend.global.s3.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GptService {
    private final RestTemplate restTemplate;
    private final GptScheduleParser gptScheduleParser;
    private final S3ImageService s3ImageService;

    @Value("${api-key.chat-gpt}")
    private String apiKey;

    public HttpEntity<GptRequestDTO> buildHttpEntity(GptRequestDTO gptRequestDTO) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(GptConfig.MEDIA_TYPE));
        httpHeaders.add(GptConfig.AUTHORIZATION, GptConfig.BEARER + apiKey);
        return new HttpEntity<>(gptRequestDTO, httpHeaders);
    }

    public GptScheduleResponseDto getResponse(String destination, HttpEntity<GptRequestDTO> chatGptRequestHttpEntity) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60 * 1000);
        restTemplate.setRequestFactory(requestFactory);

        ResponseEntity<GptResponseDTO> responseEntity = restTemplate.postForEntity(
                GptConfig.CHAT_URL,
                chatGptRequestHttpEntity,
                GptResponseDTO.class);
        String countryImage = s3ImageService.get(destination);
        List<GptSchedule> gptSchedules = gptScheduleParser.parseScheduleText(getScheduleText(responseEntity));

        return new GptScheduleResponseDto(countryImage, gptSchedules);
    }

    private String getScheduleText(ResponseEntity<GptResponseDTO> responseEntity) {
        return getGptMessage(responseEntity).toString();
    }

    private GptMessage getGptMessage(ResponseEntity<GptResponseDTO> responseEntity) {
        return responseEntity.getBody().getChoices().get(0).getMessage();
    }

    public GptScheduleResponseDto askQuestion(GptScheduleRequestDto questionRequestDTO) {
        String question = makeQuestion(questionRequestDTO);
        List<GptMessage> messages = Collections.singletonList(
                GptMessage.builder()
                        .role(GptConfig.ROLE)
                        .content(question)
                        .build()
        );


        return this.getResponse(
                questionRequestDTO.getDestination(),
                this.buildHttpEntity(
                        new GptRequestDTO(
                                GptConfig.CHAT_MODEL,
                                GptConfig.MAX_TOKEN,
                                GptConfig.TEMPERATURE,
                                GptConfig.STREAM,
                                messages
                        )
                )
        );
    }

    private String makeQuestion(GptScheduleRequestDto questionRequestDTO) {
        return String.format(GptConfig.PROMPT,
                questionRequestDTO.getDestination(),
                questionRequestDTO.getDepartureDate(),
                questionRequestDTO.getReturnDate(),
                String.join(ParsingConstants.COMMA, questionRequestDTO.getPurpose())
        );
    }
}