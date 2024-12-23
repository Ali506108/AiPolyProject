package com.ai.AVAI.module;

import lombok.Data;

@Data
public class ImageGenerationRequest {
    private String model;
    private String prompt;
    private int width;
    private int height;
    private int steps;
    private int n;
}