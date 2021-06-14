package com.leonardo.arkansasproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date;
    private String title;
    @ElementCollection
    @LazyCollection(value = LazyCollectionOption.FALSE)
    private List<String> steps;
    @ElementCollection
    @LazyCollection(value = LazyCollectionOption.FALSE)
    private List<String> attachments;
    private String expectedOutcome;
    private String actualResult;
    @Enumerated
    private ReportState state;

    public User getAuthor() {
        return User.fromId(this.userId);
    }

}
