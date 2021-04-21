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
 * Criteria class for the {@link com.sidot.gesteau.domain.Centre} entity. This class is used
 * in {@link com.sidot.gesteau.web.rest.CentreResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /centres?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CentreCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter libelle;

    private StringFilter responsable;

    private StringFilter contact;

    private LongFilter previsionId;

    private LongFilter centreRegroupementId;

    public CentreCriteria() {}

    public CentreCriteria(CentreCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.libelle = other.libelle == null ? null : other.libelle.copy();
        this.responsable = other.responsable == null ? null : other.responsable.copy();
        this.contact = other.contact == null ? null : other.contact.copy();
        this.previsionId = other.previsionId == null ? null : other.previsionId.copy();
        this.centreRegroupementId = other.centreRegroupementId == null ? null : other.centreRegroupementId.copy();
    }

    @Override
    public CentreCriteria copy() {
        return new CentreCriteria(this);
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

    public StringFilter getResponsable() {
        return responsable;
    }

    public StringFilter responsable() {
        if (responsable == null) {
            responsable = new StringFilter();
        }
        return responsable;
    }

    public void setResponsable(StringFilter responsable) {
        this.responsable = responsable;
    }

    public StringFilter getContact() {
        return contact;
    }

    public StringFilter contact() {
        if (contact == null) {
            contact = new StringFilter();
        }
        return contact;
    }

    public void setContact(StringFilter contact) {
        this.contact = contact;
    }

    public LongFilter getPrevisionId() {
        return previsionId;
    }

    public LongFilter previsionId() {
        if (previsionId == null) {
            previsionId = new LongFilter();
        }
        return previsionId;
    }

    public void setPrevisionId(LongFilter previsionId) {
        this.previsionId = previsionId;
    }

    public LongFilter getCentreRegroupementId() {
        return centreRegroupementId;
    }

    public LongFilter centreRegroupementId() {
        if (centreRegroupementId == null) {
            centreRegroupementId = new LongFilter();
        }
        return centreRegroupementId;
    }

    public void setCentreRegroupementId(LongFilter centreRegroupementId) {
        this.centreRegroupementId = centreRegroupementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CentreCriteria that = (CentreCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(libelle, that.libelle) &&
            Objects.equals(responsable, that.responsable) &&
            Objects.equals(contact, that.contact) &&
            Objects.equals(previsionId, that.previsionId) &&
            Objects.equals(centreRegroupementId, that.centreRegroupementId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, libelle, responsable, contact, previsionId, centreRegroupementId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CentreCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (libelle != null ? "libelle=" + libelle + ", " : "") +
            (responsable != null ? "responsable=" + responsable + ", " : "") +
            (contact != null ? "contact=" + contact + ", " : "") +
            (previsionId != null ? "previsionId=" + previsionId + ", " : "") +
            (centreRegroupementId != null ? "centreRegroupementId=" + centreRegroupementId + ", " : "") +
            "}";
    }
}
