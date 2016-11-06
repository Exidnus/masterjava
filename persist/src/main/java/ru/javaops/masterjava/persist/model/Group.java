package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Group extends BaseEntity {

    @NonNull private String name;
    @NonNull private GroupType type;

    public Group(Integer id, String name, GroupType type) {
        this(name, type);
        this.id = id;
    }
}
