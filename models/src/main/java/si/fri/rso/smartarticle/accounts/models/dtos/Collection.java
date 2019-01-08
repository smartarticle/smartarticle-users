package si.fri.rso.smartarticle.accounts.models.dtos;

import java.time.Instant;

public class Collection {
    private Integer id;

    private String title;

    private String description;

    private Instant creation;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Instant getCreation() {
        return creation;
    }

    public void setCreation(Instant creation) {
        this.creation = creation;
    }
}
