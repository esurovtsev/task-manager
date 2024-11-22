package com.grabduck.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        String message,
        int status,
        String error,
        String path,
        LocalDateTime timestamp,
        List<String> details
) {}
