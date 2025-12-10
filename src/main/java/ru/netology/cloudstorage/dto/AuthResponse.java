package ru.netology.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("auth-token")  // именно так требует фронт/постман в 99% дипломов Нетологии
        String authToken
) { }
