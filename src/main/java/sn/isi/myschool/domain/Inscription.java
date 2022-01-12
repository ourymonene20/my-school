package sn.isi.myschool.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Inscription.
 */
@Entity
@Table(name = "inscription")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Inscription implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_inscprion")
    private Instant dateInscprion;

    @Column(name = "annee_scolaire")
    private Instant anneeScolaire;

    @JsonIgnoreProperties(value = { "tuteur" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Eleve eleve;

    @ManyToOne
    @JsonIgnoreProperties(value = { "salle", "enseignants" }, allowSetters = true)
    private Niveau niveau;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Inscription id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateInscprion() {
        return this.dateInscprion;
    }

    public Inscription dateInscprion(Instant dateInscprion) {
        this.setDateInscprion(dateInscprion);
        return this;
    }

    public void setDateInscprion(Instant dateInscprion) {
        this.dateInscprion = dateInscprion;
    }

    public Instant getAnneeScolaire() {
        return this.anneeScolaire;
    }

    public Inscription anneeScolaire(Instant anneeScolaire) {
        this.setAnneeScolaire(anneeScolaire);
        return this;
    }

    public void setAnneeScolaire(Instant anneeScolaire) {
        this.anneeScolaire = anneeScolaire;
    }

    public Eleve getEleve() {
        return this.eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    public Inscription eleve(Eleve eleve) {
        this.setEleve(eleve);
        return this;
    }

    public Niveau getNiveau() {
        return this.niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public Inscription niveau(Niveau niveau) {
        this.setNiveau(niveau);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inscription)) {
            return false;
        }
        return id != null && id.equals(((Inscription) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inscription{" +
            "id=" + getId() +
            ", dateInscprion='" + getDateInscprion() + "'" +
            ", anneeScolaire='" + getAnneeScolaire() + "'" +
            "}";
    }
}
