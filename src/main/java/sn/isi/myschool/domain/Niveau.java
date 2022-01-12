package sn.isi.myschool.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * not an ignored comment
 */
@Schema(description = "not an ignored comment")
@Entity
@Table(name = "niveau")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Niveau implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "libelle")
    private String libelle;

    @ManyToOne
    @JsonIgnoreProperties(value = { "niveaus" }, allowSetters = true)
    private Salle salle;

    @ManyToMany(mappedBy = "niveaus")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "niveaus" }, allowSetters = true)
    private Set<Enseignant> enseignants = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Niveau id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Niveau nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Niveau libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Salle getSalle() {
        return this.salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public Niveau salle(Salle salle) {
        this.setSalle(salle);
        return this;
    }

    public Set<Enseignant> getEnseignants() {
        return this.enseignants;
    }

    public void setEnseignants(Set<Enseignant> enseignants) {
        if (this.enseignants != null) {
            this.enseignants.forEach(i -> i.removeNiveau(this));
        }
        if (enseignants != null) {
            enseignants.forEach(i -> i.addNiveau(this));
        }
        this.enseignants = enseignants;
    }

    public Niveau enseignants(Set<Enseignant> enseignants) {
        this.setEnseignants(enseignants);
        return this;
    }

    public Niveau addEnseignant(Enseignant enseignant) {
        this.enseignants.add(enseignant);
        enseignant.getNiveaus().add(this);
        return this;
    }

    public Niveau removeEnseignant(Enseignant enseignant) {
        this.enseignants.remove(enseignant);
        enseignant.getNiveaus().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Niveau)) {
            return false;
        }
        return id != null && id.equals(((Niveau) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Niveau{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
