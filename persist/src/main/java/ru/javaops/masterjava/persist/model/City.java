package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class City extends BaseEntity {

    @Column("id_str")
    @NonNull private String idStr;
    @NonNull private String name;

    public City(Integer id, String idStr, String name) {
        this(idStr, name);
        this.id = id;
    }
}
