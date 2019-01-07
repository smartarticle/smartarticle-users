package si.fri.rso.smartarticle.accounts.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import si.fri.rso.smartarticle.accounts.models.dtos.Institution;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity(name = "account")
@NamedQueries(value =
        {
                @NamedQuery(name = "Account.getAll", query = "SELECT c FROM account c"),
                @NamedQuery(name = "Account.findByInstitution", query = "SELECT o FROM account o WHERE o.instituteId = " +
                        ":instituteId")
        })
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private Instant dateOfBirth;

    private String title;

    @JsonIgnore
    @Column(name = "institute_id")
    private String instituteId;

    @Transient
    private Institution institution;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Instant dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getInstituteId() { return instituteId; }

    public void setInstituteId(String instituteId) { this.instituteId = instituteId; }

    public Institution getInstitution() { return institution; }

    public void setInstitution(Institution institute) { this.institution = institute; }
}