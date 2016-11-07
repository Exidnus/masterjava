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
    private @Column("user_id") @NonNull int cityId;

    public User(Integer id, String fullName, String email, UserFlag flag, int userId) {
        this(fullName, email, flag, userId);
        this.id=id;
    }
}