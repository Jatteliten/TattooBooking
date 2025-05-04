package com.example.tattooplatform.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelFeedback {
    SUCCESS_MESSAGE("successMessage"),
    ERROR_MESSAGE("errorMessage");

    private final String attributeKey;
}
