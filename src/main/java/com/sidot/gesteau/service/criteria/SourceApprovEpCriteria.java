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
 * Criteria class for the {@link com.sidot.gesteau.domain.SourceApprovEp} entity. This class is used
 * in {@link com.sidot.gesteau.web.rest.SourceApprovEpResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /source-approv-eps?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SourceApprovEpCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter libelle;

    private LongFilter ficheSuiviOuvrageId;

    public SourceApprovEpCriteria() {}

    public SourceApprovEpCriteria(SourceApprovEpCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.libelle = other.libelle == null ? null : other.libelle.copy();
        this.ficheSuiviOuvrageId = other.ficheSuiviOuvrageId == null ? null : other.ficheSuiviOuvrageId.copy();
    }

    @Override
    public SourceApprovEpCriteria copy() {
        return new SourceApprovEpCriteria(this);
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

    public StringFilter getLibelle() {
        return libelle;
    }

    public StringFilter libelle() {
        if (libelle == null) {
            libelle = new StringFilter();
        }
        return libelle;
    }

    public void setLibelle(StringFilter libelle) {
        this.libelle = libelle;
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
        final SourceApprovEpCriteria that = (SourceApprovEpCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(libelle, that.libelle) &&
            Objects.equals(ficheSuiviOuvrageId, that.ficheSuiviOuvrageId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, libelle, ficheSuiviOuvrageId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SourceApprovEpCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (libelle != null ? "libelle=" + libelle + ", " : "") +
            (ficheSuiviOuvrageId != null ? "ficheSuiviOuvrageId=" + ficheSuiviOuvrageId + ", " : "") +
            "}";
    }
}
