package sn.isi.myschool.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tuteurs.
 */
@Entity
@Table(name = "tuteurs")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tuteurs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @NotNull
    @Column(name = "telephone", nullable = false)
    private String telephone;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "tuteur")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tuteur" }, allowSetters = true)
    private Set<Eleve> eleves = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tuteurs id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Tuteurs nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Tuteurs prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Tuteurs telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return this.email;
    }

    public Tuteurs email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Eleve> getEleves() {
        return this.eleves;
    }

    public void setEleves(Set<Eleve> eleves) {
        if (this.eleves != null) {
            this.eleves.forEach(i -> i.setTuteur(null));
        }
        if (eleves != null) {
            eleves.forEach(i -> i.setTuteur(this));
        }
        this.eleves = eleves;
    }

    public Tuteurs eleves(Set<Eleve> eleves) {
        this.setEleves(eleves);
        return this;
    }

    public Tuteurs addEleves(Eleve eleve) {
        this.eleves.add(eleve);
        eleve.setTuteur(this);
        return this;
    }

    public Tuteurs removeEleves(Eleve eleve) {
        this.eleves.remove(eleve);
        eleve.setTuteur(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuteurs)) {
            return false;
        }
        return id != null && id.equals(((Tuteurs) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tuteurs{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
