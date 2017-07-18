package com.github.xabgesagtx.mensa.config;

import com.github.xabgesagtx.mensa.model.Mensa;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Main configuration for the mensas.
 *
 * Currently only supports configuring which mensa is open on Saturday
 */
@Configuration
@ConfigurationProperties(prefix = "mensa")
@Getter
@Setter
public class MensaConfig {

    private List<String> idsOpenOnSaturday = Arrays.asList("350");

    public boolean isMensaOpenOnSaturday(Mensa mensa) {
        return idsOpenOnSaturday.contains(mensa.getId());
    }

    public boolean isMensaOpenOnSaturday(String mensaId) {
        return idsOpenOnSaturday.contains(mensaId);
    }

}
