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
 * A Prevision.
 */
@Entity
@Table(name = "prevision")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "prevision")
public class Prevision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "nb_latrine", nullable = false)
    private Integer nbLatrine;

    @NotNull
    @Column(name = "nb_puisard", nullable = false)
    private Integer nbPuisard;

    @NotNull
    @Column(name = "nb_public", nullable = false)
    private Integer nbPublic;

    @NotNull
    @Column(name = "nb_scolaire", nullable = false)
    private Integer nbScolaire;

    @JsonIgnoreProperties(value = { "prevision" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Annee annee;

    @JsonIgnoreProperties(value = { "prevision", "centreRegroupement" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Centre centre;

    @OneToMany(mappedBy = "prevision")
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

    public Prevision id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getNbLatrine() {
        return this.nbLatrine;
    }

    public Prevision nbLatrine(Integer nbLatrine) {
        this.nbLatrine = nbLatrine;
        return this;
    }

    public void setNbLatrine(Integer nbLatrine) {
        this.nbLatrine = nbLatrine;
    }

    public Integer getNbPuisard() {
        return this.nbPuisard;
    }

    public Prevision nbPuisard(Integer nbPuisard) {
        this.nbPuisard = nbPuisard;
        return this;
    }

    public void setNbPuisard(Integer nbPuisard) {
        this.nbPuisard = nbPuisard;
    }

    public Integer getNbPublic() {
        return this.nbPublic;
    }

    public Prevision nbPublic(Integer nbPublic) {
        this.nbPublic = nbPublic;
        return this;
    }

    public void setNbPublic(Integer nbPublic) {
        this.nbPublic = nbPublic;
    }

    public Integer getNbScolaire() {
        return this.nbScolaire;
    }

    public Prevision nbScolaire(Integer nbScolaire) {
        this.nbScolaire = nbScolaire;
        return this;
    }

    public void setNbScolaire(Integer nbScolaire) {
        this.nbScolaire = nbScolaire;
    }

    public Annee getAnnee() {
        return this.annee;
    }

    public Prevision annee(Annee annee) {
        this.setAnnee(annee);
        return this;
    }

    public void setAnnee(Annee annee) {
        this.annee = annee;
    }

    public Centre getCentre() {
        return this.centre;
    }

    public Prevision centre(Centre centre) {
        this.setCentre(centre);
        return this;
    }

    public void setCentre(Centre centre) {
        this.centre = centre;
    }

    public Set<FicheSuiviOuvrage> getFicheSuiviOuvrages() {
        return this.ficheSuiviOuvrages;
    }

    public Prevision ficheSuiviOuvrages(Set<FicheSuiviOuvrage> ficheSuiviOuvrages) {
        this.setFicheSuiviOuvrages(ficheSuiviOuvrages);
        return this;
    }

    public Prevision addFicheSuiviOuvrage(FicheSuiviOuvrage ficheSuiviOuvrage) {
        this.ficheSuiviOuvrages.add(ficheSuiviOuvrage);
        ficheSuiviOuvrage.setPrevision(this);
        return this;
    }

    public Prevision removeFicheSuiviOuvrage(FicheSuiviOuvrage ficheSuiviOuvrage) {
        this.ficheSuiviOuvrages.remove(ficheSuiviOuvrage);
        ficheSuiviOuvrage.setPrevision(null);
        return this;
    }

    public void setFicheSuiviOuvrages(Set<FicheSuiviOuvrage> ficheSuiviOuvrages) {
        if (this.ficheSuiviOuvrages != null) {
            this.ficheSuiviOuvrages.forEach(i -> i.setPrevision(null));
        }
        if (ficheSuiviOuvrages != null) {
            ficheSuiviOuvrages.forEach(i -> i.setPrevision(this));
        }
        this.ficheSuiviOuvrages = ficheSuiviOuvrages;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Prevision)) {
            return false;
        }
        return id != null && id.equals(((Prevision) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Prevision{" +
            "id=" + getId() +
            ", nbLatrine=" + getNbLatrine() +
            ", nbPuisard=" + getNbPuisard() +
            ", nbPublic=" + getNbPublic() +
            ", nbScolaire=" + getNbScolaire() +
            "}";
    }
}
