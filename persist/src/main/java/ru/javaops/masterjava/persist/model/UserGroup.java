package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class UserGroup {
    @NonNull @Column("user_id") private Integer userId;
    @NonNull @Column("group_id") private Integer groupId;
}
