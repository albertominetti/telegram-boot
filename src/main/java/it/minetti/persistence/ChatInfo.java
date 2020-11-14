package it.minetti.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Locale;

@NoArgsConstructor
@Data
@Entity
@Table(name = "CHAT")
public class ChatInfo {

    @Id
    @Column(name = "CHAT_ID")
    private String chatId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "LOCALE")
    private Locale locale;

    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "FIRST_SEEN")
    private Instant firstSeen;

}
