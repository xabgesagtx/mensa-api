package com.github.xabgesagtx.mensa.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model for a label of a dish
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@ToString
@EqualsAndHashCode
@Document(collection = "label")
public class Label {

    @Id
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String imageUrl;

}
