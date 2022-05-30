package org.planqk.gateway.dtos;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class QpuSelectionDto {

    public boolean simulatorsAllowed;

    public List<String> allowedProviders;

    public String circuitLanguage;

    public URL circuitUrl;

    public Map<String, String> tokens;

    public String refreshToken;

    public String circuitName;

    public String userId;
}
