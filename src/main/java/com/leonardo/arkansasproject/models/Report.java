package com.leonardo.arkansasproject.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leonardo.arkansasproject.Bot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Report implements Serializable {

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
    private List<String> steps = Lists.newArrayList();
    @ElementCollection
    @CollectionTable(name = "reports_attachments",
                     joinColumns = {@JoinColumn(name = "attachment_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "attachment_name")
    @Column(name = "url")
    private HashMap<String, String> attachments = Maps.newHashMap();
    private String expectedOutcome;
    private String actualResult;
    private String serverName;
    @Enumerated
    private ReportState state;

    public User getAuthor() {
        return Bot.getInstance().getJda().retrieveUserById(userId).complete();
    }

    public void appendStep(String step) {
        this.steps.add(step);
    }

    public void attach(String description, String url) {
        this.attachments.put(description, url);
    }

}
