package sn.isi.myschool.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Salle.
 */
@Entity
@Table(name = "salle")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Salle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "capacite")
    private Integer capacite;

    @OneToMany(mappedBy = "salle")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "salle", "enseignants" }, allowSetters = true)
    private Set<Niveau> niveaus = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Salle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Salle libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getCapacite() {
        return this.capacite;
    }

    public Salle capacite(Integer capacite) {
        this.setCapacite(capacite);
        return this;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public Set<Niveau> getNiveaus() {
        return this.niveaus;
    }

    public void setNiveaus(Set<Niveau> niveaus) {
        if (this.niveaus != null) {
            this.niveaus.forEach(i -> i.setSalle(null));
        }
        if (niveaus != null) {
            niveaus.forEach(i -> i.setSalle(this));
        }
        this.niveaus = niveaus;
    }

    public Salle niveaus(Set<Niveau> niveaus) {
        this.setNiveaus(niveaus);
        return this;
    }

    public Salle addNiveau(Niveau niveau) {
        this.niveaus.add(niveau);
        niveau.setSalle(this);
        return this;
    }

    public Salle removeNiveau(Niveau niveau) {
        this.niveaus.remove(niveau);
        niveau.setSalle(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Salle)) {
            return false;
        }
        return id != null && id.equals(((Salle) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Salle{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", capacite=" + getCapacite() +
            "}";
    }
}
