package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Project extends BaseEntity {

    @NonNull private String name;
    @NonNull private String description;

    public Project(Integer id, String name, String description) {
        this(name, description);
        this.id = id;
    }
}
