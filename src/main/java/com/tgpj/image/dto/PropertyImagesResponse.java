package com.tgpj.image.dto;

import java.util.List;

import com.tgpj.property.dto.PropertyImageInfo;

public record PropertyImagesResponse(List<PropertyImageInfo> images) {
}
