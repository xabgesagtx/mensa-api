package com.github.xabgesagtx.mensa.model;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Class to hold information of a bot user
 */
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Document(collection = "botuser")
public class BotUser {

    @Id
    private Long chatId;
    private String mensaId;
    public boolean hasSelectedMensa() {
        return StringUtils.isNotBlank(mensaId);
    }

}
