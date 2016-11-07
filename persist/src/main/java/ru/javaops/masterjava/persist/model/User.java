package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {

    private @Column("full_name") @NonNull String fullName;
    private @NonNull String email;
    private @NonNull UserFlag flag;
    private @Column("city_id") @NonNull Integer cityId;

    public User(Integer id, String fullName, String email, UserFlag flag, Integer userId) {
        this(fullName, email, flag, userId);
        this.id=id;
    }
}