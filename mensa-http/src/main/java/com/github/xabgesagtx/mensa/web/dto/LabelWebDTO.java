package com.github.xabgesagtx.mensa.web.dto;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * DTO for a label
 */
@Getter
@Builder
public class LabelWebDTO {

    private String name;
    private String imageUrl;

    /**
     * Tells if an image is available
     * @return true if image is present, false otherwise
     */
    public boolean isWithImage() {
        return StringUtils.isNotBlank(imageUrl);
    }
}
