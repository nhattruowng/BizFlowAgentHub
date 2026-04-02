package com.bizflow.shared.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotBlank
    private String tenantId;
    @NotBlank
    private String source;
    @NotBlank
    private String type;
    @NotNull
    private Map<String, Object> payload;
    private String correlationId;
}
