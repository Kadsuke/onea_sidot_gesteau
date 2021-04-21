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
 * Criteria class for the {@link com.sidot.gesteau.domain.CentreRegroupement} entity. This class is used
 * in {@link com.sidot.gesteau.web.rest.CentreRegroupementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /centre-regroupements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CentreRegroupementCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter libelle;

    private StringFilter responsable;

    private StringFilter contact;

    private LongFilter centreId;

    private LongFilter directionRegionaleId;

    public CentreRegroupementCriteria() {}

    public CentreRegroupementCriteria(CentreRegroupementCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.libelle = other.libelle == null ? null : other.libelle.copy();
        this.responsable = other.responsable == null ? null : other.responsable.copy();
        this.contact = other.contact == null ? null : other.contact.copy();
        this.centreId = other.centreId == null ? null : other.centreId.copy();
        this.directionRegionaleId = other.directionRegionaleId == null ? null : other.directionRegionaleId.copy();
    }

    @Override
    public CentreRegroupementCriteria copy() {
        return new CentreRegroupementCriteria(this);
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

    public LongFilter getDirectionRegionaleId() {
        return directionRegionaleId;
    }

    public LongFilter directionRegionaleId() {
        if (directionRegionaleId == null) {
            directionRegionaleId = new LongFilter();
        }
        return directionRegionaleId;
    }

    public void setDirectionRegionaleId(LongFilter directionRegionaleId) {
        this.directionRegionaleId = directionRegionaleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CentreRegroupementCriteria that = (CentreRegroupementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(libelle, that.libelle) &&
            Objects.equals(responsable, that.responsable) &&
            Objects.equals(contact, that.contact) &&
            Objects.equals(centreId, that.centreId) &&
            Objects.equals(directionRegionaleId, that.directionRegionaleId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, libelle, responsable, contact, centreId, directionRegionaleId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CentreRegroupementCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (libelle != null ? "libelle=" + libelle + ", " : "") +
            (responsable != null ? "responsable=" + responsable + ", " : "") +
            (contact != null ? "contact=" + contact + ", " : "") +
            (centreId != null ? "centreId=" + centreId + ", " : "") +
            (directionRegionaleId != null ? "directionRegionaleId=" + directionRegionaleId + ", " : "") +
            "}";
    }
}
