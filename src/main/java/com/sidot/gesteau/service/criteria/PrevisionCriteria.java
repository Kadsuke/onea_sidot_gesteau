package com.sidot.gesteau.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.sidot.gesteau.domain.Prevision} entity. This class is used
 * in {@link com.sidot.gesteau.web.rest.PrevisionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /previsions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PrevisionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter nbLatrine;

    private IntegerFilter nbPuisard;

    private IntegerFilter nbPublic;

    private IntegerFilter nbScolaire;

    private LongFilter anneeId;

    private LongFilter centreId;

    private LongFilter ficheSuiviOuvrageId;

    public PrevisionCriteria() {}

    public PrevisionCriteria(PrevisionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nbLatrine = other.nbLatrine == null ? null : other.nbLatrine.copy();
        this.nbPuisard = other.nbPuisard == null ? null : other.nbPuisard.copy();
        this.nbPublic = other.nbPublic == null ? null : other.nbPublic.copy();
        this.nbScolaire = other.nbScolaire == null ? null : other.nbScolaire.copy();
        this.anneeId = other.anneeId == null ? null : other.anneeId.copy();
        this.centreId = other.centreId == null ? null : other.centreId.copy();
        this.ficheSuiviOuvrageId = other.ficheSuiviOuvrageId == null ? null : other.ficheSuiviOuvrageId.copy();
    }

    @Override
    public PrevisionCriteria copy() {
        return new PrevisionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getNbLatrine() {
        return nbLatrine;
    }

    public IntegerFilter nbLatrine() {
        if (nbLatrine == null) {
            nbLatrine = new IntegerFilter();
        }
        return nbLatrine;
    }

    public void setNbLatrine(IntegerFilter nbLatrine) {
        this.nbLatrine = nbLatrine;
    }

    public IntegerFilter getNbPuisard() {
        return nbPuisard;
    }

    public IntegerFilter nbPuisard() {
        if (nbPuisard == null) {
            nbPuisard = new IntegerFilter();
        }
        return nbPuisard;
    }

    public void setNbPuisard(IntegerFilter nbPuisard) {
        this.nbPuisard = nbPuisard;
    }

    public IntegerFilter getNbPublic() {
        return nbPublic;
    }

    public IntegerFilter nbPublic() {
        if (nbPublic == null) {
            nbPublic = new IntegerFilter();
        }
        return nbPublic;
    }

    public void setNbPublic(IntegerFilter nbPublic) {
        this.nbPublic = nbPublic;
    }

    public IntegerFilter getNbScolaire() {
        return nbScolaire;
    }

    public IntegerFilter nbScolaire() {
        if (nbScolaire == null) {
            nbScolaire = new IntegerFilter();
        }
        return nbScolaire;
    }

    public void setNbScolaire(IntegerFilter nbScolaire) {
        this.nbScolaire = nbScolaire;
    }

    public LongFilter getAnneeId() {
        return anneeId;
    }

    public LongFilter anneeId() {
        if (anneeId == null) {
            anneeId = new LongFilter();
        }
        return anneeId;
    }

    public void setAnneeId(LongFilter anneeId) {
        this.anneeId = anneeId;
    }

    public LongFilter getCentreId() {
        return centreId;
    }

    public LongFilter centreId() {
        if (centreId == null) {
            centreId = new LongFilter();
        }
        return centreId;
    }

    public void setCentreId(LongFilter centreId) {
        this.centreId = centreId;
    }

    public LongFilter getFicheSuiviOuvrageId() {
        return ficheSuiviOuvrageId;
    }

    public LongFilter ficheSuiviOuvrageId() {
        if (ficheSuiviOuvrageId == null) {
            ficheSuiviOuvrageId = new LongFilter();
        }
        return ficheSuiviOuvrageId;
    }

    public void setFicheSuiviOuvrageId(LongFilter ficheSuiviOuvrageId) {
        this.ficheSuiviOuvrageId = ficheSuiviOuvrageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PrevisionCriteria that = (PrevisionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nbLatrine, that.nbLatrine) &&
            Objects.equals(nbPuisard, that.nbPuisard) &&
            Objects.equals(nbPublic, that.nbPublic) &&
            Objects.equals(nbScolaire, that.nbScolaire) &&
            Objects.equals(anneeId, that.anneeId) &&
            Objects.equals(centreId, that.centreId) &&
            Objects.equals(ficheSuiviOuvrageId, that.ficheSuiviOuvrageId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nbLatrine, nbPuisard, nbPublic, nbScolaire, anneeId, centreId, ficheSuiviOuvrageId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrevisionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nbLatrine != null ? "nbLatrine=" + nbLatrine + ", " : "") +
            (nbPuisard != null ? "nbPuisard=" + nbPuisard + ", " : "") +
            (nbPublic != null ? "nbPublic=" + nbPublic + ", " : "") +
            (nbScolaire != null ? "nbScolaire=" + nbScolaire + ", " : "") +
            (anneeId != null ? "anneeId=" + anneeId + ", " : "") +
            (centreId != null ? "centreId=" + centreId + ", " : "") +
            (ficheSuiviOuvrageId != null ? "ficheSuiviOuvrageId=" + ficheSuiviOuvrageId + ", " : "") +
            "}";
    }
}
