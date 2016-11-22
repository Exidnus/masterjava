package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Created by dmitriy_varygin on 21.11.16.
 */
@Data
@NoArgsConstructor
//@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SendEmailResult extends BaseEntity {

    @Getter @Column("email_address") private String emailAddress;
    @Getter @Column("is_succeed") private boolean succeed;
    @Getter @Column("fail_cause") private String failCause;
    //@Getter @Column("date_time") private LocalDateTime dateTime;
    @Getter @Setter @Column("id_email") private Integer emailId;

    public SendEmailResult(Integer id, String emailAddress, boolean succeed, String failCause, Integer emailId) {
        super(id);
        this.emailAddress = emailAddress;
        this.succeed = succeed;
        this.failCause = failCause;
        this.emailId = emailId;
    }

    public SendEmailResult(Integer id, String emailAddress, boolean succeed, Integer emailId) {
        this(id, emailAddress, succeed, null, emailId);
    }
}
