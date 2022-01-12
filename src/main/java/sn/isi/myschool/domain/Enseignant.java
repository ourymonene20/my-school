package sn.isi.myschool.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Enseignant.
 */
@Entity
@Table(name = "enseignant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Enseignant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "email")
    private String email;

    @Column(name = "date_naiss")
    private Instant dateNaiss;

    @ManyToMany
    @JoinTable(
        name = "rel_enseignant__niveau",
        joinColumns = @JoinColumn(name = "enseignant_id"),
        inverseJoinColumns = @JoinColumn(name = "niveau_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "salle", "enseignants" }, allowSetters = true)
    private Set<Niveau> niveaus = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Enseignant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Enseignant nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Enseignant prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return this.email;
    }

    public Enseignant email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getDateNaiss() {
        return this.dateNaiss;
    }

    public Enseignant dateNaiss(Instant dateNaiss) {
        this.setDateNaiss(dateNaiss);
        return this;
    }

    public void setDateNaiss(Instant dateNaiss) {
        this.dateNaiss = dateNaiss;
    }

    public Set<Niveau> getNiveaus() {
        return this.niveaus;
    }

    public void setNiveaus(Set<Niveau> niveaus) {
        this.niveaus = niveaus;
    }

    public Enseignant niveaus(Set<Niveau> niveaus) {
        this.setNiveaus(niveaus);
        return this;
    }

    public Enseignant addNiveau(Niveau niveau) {
        this.niveaus.add(niveau);
        niveau.getEnseignants().add(this);
        return this;
    }

    public Enseignant removeNiveau(Niveau niveau) {
        this.niveaus.remove(niveau);
        niveau.getEnseignants().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enseignant)) {
            return false;
        }
        return id != null && id.equals(((Enseignant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Enseignant{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", email='" + getEmail() + "'" +
            ", dateNaiss='" + getDateNaiss() + "'" +
            "}";
    }
}
