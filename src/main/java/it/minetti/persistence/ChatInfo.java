package it.minetti.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Locale;
import java.util.StringJoiner;

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

    @Override
    public String toString() {
        return new StringJoiner(", ", ChatInfo.class.getSimpleName() + "[", "]")
                .add("chatId='" + chatId + "'")
                .add("status='" + status + "'")
                .add("locale='" + locale + "'")
                .toString();
    }
}
