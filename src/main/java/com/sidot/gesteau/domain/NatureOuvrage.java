package com.sidot.gesteau.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A NatureOuvrage.
 */
@Entity
@Table(name = "nature_ouvrage")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "natureouvrage")
public class NatureOuvrage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "libelle", nullable = false)
    private String libelle;

    @OneToMany(mappedBy = "natureouvrage")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = {
            "prevision",
            "natureouvrage",
            "typehabitation",
            "sourceapprovep",
            "modeevacuationeauusee",
            "modeevacexcreta",
            "macon",
            "prefabricant",
        },
        allowSetters = true
    )
    private Set<FicheSuiviOuvrage> ficheSuiviOuvrages = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NatureOuvrage id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public NatureOuvrage libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Set<FicheSuiviOuvrage> getFicheSuiviOuvrages() {
        return this.ficheSuiviOuvrages;
    }

    public NatureOuvrage ficheSuiviOuvrages(Set<FicheSuiviOuvrage> ficheSuiviOuvrages) {
        this.setFicheSuiviOuvrages(ficheSuiviOuvrages);
        return this;
    }

    public NatureOuvrage addFicheSuiviOuvrage(FicheSuiviOuvrage ficheSuiviOuvrage) {
        this.ficheSuiviOuvrages.add(ficheSuiviOuvrage);
        ficheSuiviOuvrage.setNatureouvrage(this);
        return this;
    }

    public NatureOuvrage removeFicheSuiviOuvrage(FicheSuiviOuvrage ficheSuiviOuvrage) {
        this.ficheSuiviOuvrages.remove(ficheSuiviOuvrage);
        ficheSuiviOuvrage.setNatureouvrage(null);
        return this;
    }

    public void setFicheSuiviOuvrages(Set<FicheSuiviOuvrage> ficheSuiviOuvrages) {
        if (this.ficheSuiviOuvrages != null) {
            this.ficheSuiviOuvrages.forEach(i -> i.setNatureouvrage(null));
        }
        if (ficheSuiviOuvrages != null) {
            ficheSuiviOuvrages.forEach(i -> i.setNatureouvrage(this));
        }
        this.ficheSuiviOuvrages = ficheSuiviOuvrages;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NatureOuvrage)) {
            return false;
        }
        return id != null && id.equals(((NatureOuvrage) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NatureOuvrage{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
