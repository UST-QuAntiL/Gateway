package org.planqk.gateway.dtos;

import java.util.Map;
import java.util.UUID;

public class SelectionRequestDto {

    public Map<String, String> parameters;

    public UUID algorithmId;

    public String refreshToken;
}
