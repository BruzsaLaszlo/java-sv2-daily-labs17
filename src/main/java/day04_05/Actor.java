package day04_05;

import lombok.*;

@Getter
@AllArgsConstructor
@NonNull
@EqualsAndHashCode()
public class Actor {

    @Setter
    private Long id;

    @NonNull
    private String name;

    public Actor(String name) {
        this.name = name;
    }


}
