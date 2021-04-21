package com.sidot.gesteau.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.sidot.gesteau.domain.FicheSuiviOuvrage} entity. This class is used
 * in {@link com.sidot.gesteau.web.rest.FicheSuiviOuvrageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /fiche-suivi-ouvrages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FicheSuiviOuvrageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter prjAppuis;

    private StringFilter nomBenef;

    private StringFilter prenomBenef;

    private StringFilter professionBenef;

    private LongFilter nbUsagers;

    private StringFilter contacts;

    private FloatFilter longitude;

    private FloatFilter latitude;

    private InstantFilter dateRemiseDevis;

    private InstantFilter dateDebutTravaux;

    private InstantFilter dateFinTravaux;

    private StringFilter rue;

    private IntegerFilter porte;

    private StringFilter coutMenage;

    private IntegerFilter subvOnea;

    private IntegerFilter subvProjet;

    private IntegerFilter autreSubv;

    private IntegerFilter toles;

    private StringFilter animateur;

    private StringFilter superviseur;

    private StringFilter controleur;

    private LongFilter previsionId;

    private LongFilter natureouvrageId;

    private LongFilter typehabitationId;

    private LongFilter sourceapprovepId;

    private LongFilter modeevacuationeauuseeId;

    private LongFilter modeevacexcretaId;

    private LongFilter maconId;

    private LongFilter prefabricantId;

    public FicheSuiviOuvrageCriteria() {}

    public FicheSuiviOuvrageCriteria(FicheSuiviOuvrageCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.prjAppuis = other.prjAppuis == null ? null : other.prjAppuis.copy();
        this.nomBenef = other.nomBenef == null ? null : other.nomBenef.copy();
        this.prenomBenef = other.prenomBenef == null ? null : other.prenomBenef.copy();
        this.professionBenef = other.professionBenef == null ? null : other.professionBenef.copy();
        this.nbUsagers = other.nbUsagers == null ? null : other.nbUsagers.copy();
        this.contacts = other.contacts == null ? null : other.contacts.copy();
        this.longitude = other.longitude == null ? null : other.longitude.copy();
        this.latitude = other.latitude == null ? null : other.latitude.copy();
        this.dateRemiseDevis = other.dateRemiseDevis == null ? null : other.dateRemiseDevis.copy();
        this.dateDebutTravaux = other.dateDebutTravaux == null ? null : other.dateDebutTravaux.copy();
        this.dateFinTravaux = other.dateFinTravaux == null ? null : other.dateFinTravaux.copy();
        this.rue = other.rue == null ? null : other.rue.copy();
        this.porte = other.porte == null ? null : other.porte.copy();
        this.coutMenage = other.coutMenage == null ? null : other.coutMenage.copy();
        this.subvOnea = other.subvOnea == null ? null : other.subvOnea.copy();
        this.subvProjet = other.subvProjet == null ? null : other.subvProjet.copy();
        this.autreSubv = other.autreSubv == null ? null : other.autreSubv.copy();
        this.toles = other.toles == null ? null : other.toles.copy();
        this.animateur = other.animateur == null ? null : other.animateur.copy();
        this.superviseur = other.superviseur == null ? null : other.superviseur.copy();
        this.controleur = other.controleur == null ? null : other.controleur.copy();
        this.previsionId = other.previsionId == null ? null : other.previsionId.copy();
        this.natureouvrageId = other.natureouvrageId == null ? null : other.natureouvrageId.copy();
        this.typehabitationId = other.typehabitationId == null ? null : other.typehabitationId.copy();
        this.sourceapprovepId = other.sourceapprovepId == null ? null : other.sourceapprovepId.copy();
        this.modeevacuationeauuseeId = other.modeevacuationeauuseeId == null ? null : other.modeevacuationeauuseeId.copy();
        this.modeevacexcretaId = other.modeevacexcretaId == null ? null : other.modeevacexcretaId.copy();
        this.maconId = other.maconId == null ? null : other.maconId.copy();
        this.prefabricantId = other.prefabricantId == null ? null : other.prefabricantId.copy();
    }

    @Override
    public FicheSuiviOuvrageCriteria copy() {
        return new FicheSuiviOuvrageCriteria(this);
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

    public StringFilter getPrjAppuis() {
        return prjAppuis;
    }

    public StringFilter prjAppuis() {
        if (prjAppuis == null) {
            prjAppuis = new StringFilter();
        }
        return prjAppuis;
    }

    public void setPrjAppuis(StringFilter prjAppuis) {
        this.prjAppuis = prjAppuis;
    }

    public StringFilter getNomBenef() {
        return nomBenef;
    }

    public StringFilter nomBenef() {
        if (nomBenef == null) {
            nomBenef = new StringFilter();
        }
        return nomBenef;
    }

    public void setNomBenef(StringFilter nomBenef) {
        this.nomBenef = nomBenef;
    }

    public StringFilter getPrenomBenef() {
        return prenomBenef;
    }

    public StringFilter prenomBenef() {
        if (prenomBenef == null) {
            prenomBenef = new StringFilter();
        }
        return prenomBenef;
    }

    public void setPrenomBenef(StringFilter prenomBenef) {
        this.prenomBenef = prenomBenef;
    }

    public StringFilter getProfessionBenef() {
        return professionBenef;
    }

    public StringFilter professionBenef() {
        if (professionBenef == null) {
            professionBenef = new StringFilter();
        }
        return professionBenef;
    }

    public void setProfessionBenef(StringFilter professionBenef) {
        this.professionBenef = professionBenef;
    }

    public LongFilter getNbUsagers() {
        return nbUsagers;
    }

    public LongFilter nbUsagers() {
        if (nbUsagers == null) {
            nbUsagers = new LongFilter();
        }
        return nbUsagers;
    }

    public void setNbUsagers(LongFilter nbUsagers) {
        this.nbUsagers = nbUsagers;
    }

    public StringFilter getContacts() {
        return contacts;
    }

    public StringFilter contacts() {
        if (contacts == null) {
            contacts = new StringFilter();
        }
        return contacts;
    }

    public void setContacts(StringFilter contacts) {
        this.contacts = contacts;
    }

    public FloatFilter getLongitude() {
        return longitude;
    }

    public FloatFilter longitude() {
        if (longitude == null) {
            longitude = new FloatFilter();
        }
        return longitude;
    }

    public void setLongitude(FloatFilter longitude) {
        this.longitude = longitude;
    }

    public FloatFilter getLatitude() {
        return latitude;
    }

    public FloatFilter latitude() {
        if (latitude == null) {
            latitude = new FloatFilter();
        }
        return latitude;
    }

    public void setLatitude(FloatFilter latitude) {
        this.latitude = latitude;
    }

    public InstantFilter getDateRemiseDevis() {
        return dateRemiseDevis;
    }

    public InstantFilter dateRemiseDevis() {
        if (dateRemiseDevis == null) {
            dateRemiseDevis = new InstantFilter();
        }
        return dateRemiseDevis;
    }

    public void setDateRemiseDevis(InstantFilter dateRemiseDevis) {
        this.dateRemiseDevis = dateRemiseDevis;
    }

    public InstantFilter getDateDebutTravaux() {
        return dateDebutTravaux;
    }

    public InstantFilter dateDebutTravaux() {
        if (dateDebutTravaux == null) {
            dateDebutTravaux = new InstantFilter();
        }
        return dateDebutTravaux;
    }

    public void setDateDebutTravaux(InstantFilter dateDebutTravaux) {
        this.dateDebutTravaux = dateDebutTravaux;
    }

    public InstantFilter getDateFinTravaux() {
        return dateFinTravaux;
    }

    public InstantFilter dateFinTravaux() {
        if (dateFinTravaux == null) {
            dateFinTravaux = new InstantFilter();
        }
        return dateFinTravaux;
    }

    public void setDateFinTravaux(InstantFilter dateFinTravaux) {
        this.dateFinTravaux = dateFinTravaux;
    }

    public StringFilter getRue() {
        return rue;
    }

    public StringFilter rue() {
        if (rue == null) {
            rue = new StringFilter();
        }
        return rue;
    }

    public void setRue(StringFilter rue) {
        this.rue = rue;
    }

    public IntegerFilter getPorte() {
        return porte;
    }

    public IntegerFilter porte() {
        if (porte == null) {
            porte = new IntegerFilter();
        }
        return porte;
    }

    public void setPorte(IntegerFilter porte) {
        this.porte = porte;
    }

    public StringFilter getCoutMenage() {
        return coutMenage;
    }

    public StringFilter coutMenage() {
        if (coutMenage == null) {
            coutMenage = new StringFilter();
        }
        return coutMenage;
    }

    public void setCoutMenage(StringFilter coutMenage) {
        this.coutMenage = coutMenage;
    }

    public IntegerFilter getSubvOnea() {
        return subvOnea;
    }

    public IntegerFilter subvOnea() {
        if (subvOnea == null) {
            subvOnea = new IntegerFilter();
        }
        return subvOnea;
    }

    public void setSubvOnea(IntegerFilter subvOnea) {
        this.subvOnea = subvOnea;
    }

    public IntegerFilter getSubvProjet() {
        return subvProjet;
    }

    public IntegerFilter subvProjet() {
        if (subvProjet == null) {
            subvProjet = new IntegerFilter();
        }
        return subvProjet;
    }

    public void setSubvProjet(IntegerFilter subvProjet) {
        this.subvProjet = subvProjet;
    }

    public IntegerFilter getAutreSubv() {
        return autreSubv;
    }

    public IntegerFilter autreSubv() {
        if (autreSubv == null) {
            autreSubv = new IntegerFilter();
        }
        return autreSubv;
    }

    public void setAutreSubv(IntegerFilter autreSubv) {
        this.autreSubv = autreSubv;
    }

    public IntegerFilter getToles() {
        return toles;
    }

    public IntegerFilter toles() {
        if (toles == null) {
            toles = new IntegerFilter();
        }
        return toles;
    }

    public void setToles(IntegerFilter toles) {
        this.toles = toles;
    }

    public StringFilter getAnimateur() {
        return animateur;
    }

    public StringFilter animateur() {
        if (animateur == null) {
            animateur = new StringFilter();
        }
        return animateur;
    }

    public void setAnimateur(StringFilter animateur) {
        this.animateur = animateur;
    }

    public StringFilter getSuperviseur() {
        return superviseur;
    }

    public StringFilter superviseur() {
        if (superviseur == null) {
            superviseur = new StringFilter();
        }
        return superviseur;
    }

    public void setSuperviseur(StringFilter superviseur) {
        this.superviseur = superviseur;
    }

    public StringFilter getControleur() {
        return controleur;
    }

    public StringFilter controleur() {
        if (controleur == null) {
            controleur = new StringFilter();
        }
        return controleur;
    }

    public void setControleur(StringFilter controleur) {
        this.controleur = controleur;
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

    public LongFilter getNatureouvrageId() {
        return natureouvrageId;
    }

    public LongFilter natureouvrageId() {
        if (natureouvrageId == null) {
            natureouvrageId = new LongFilter();
        }
        return natureouvrageId;
    }

    public void setNatureouvrageId(LongFilter natureouvrageId) {
        this.natureouvrageId = natureouvrageId;
    }

    public LongFilter getTypehabitationId() {
        return typehabitationId;
    }

    public LongFilter typehabitationId() {
        if (typehabitationId == null) {
            typehabitationId = new LongFilter();
        }
        return typehabitationId;
    }

    public void setTypehabitationId(LongFilter typehabitationId) {
        this.typehabitationId = typehabitationId;
    }

    public LongFilter getSourceapprovepId() {
        return sourceapprovepId;
    }

    public LongFilter sourceapprovepId() {
        if (sourceapprovepId == null) {
            sourceapprovepId = new LongFilter();
        }
        return sourceapprovepId;
    }

    public void setSourceapprovepId(LongFilter sourceapprovepId) {
        this.sourceapprovepId = sourceapprovepId;
    }

    public LongFilter getModeevacuationeauuseeId() {
        return modeevacuationeauuseeId;
    }

    public LongFilter modeevacuationeauuseeId() {
        if (modeevacuationeauuseeId == null) {
            modeevacuationeauuseeId = new LongFilter();
        }
        return modeevacuationeauuseeId;
    }

    public void setModeevacuationeauuseeId(LongFilter modeevacuationeauuseeId) {
        this.modeevacuationeauuseeId = modeevacuationeauuseeId;
    }

    public LongFilter getModeevacexcretaId() {
        return modeevacexcretaId;
    }

    public LongFilter modeevacexcretaId() {
        if (modeevacexcretaId == null) {
            modeevacexcretaId = new LongFilter();
        }
        return modeevacexcretaId;
    }

    public void setModeevacexcretaId(LongFilter modeevacexcretaId) {
        this.modeevacexcretaId = modeevacexcretaId;
    }

    public LongFilter getMaconId() {
        return maconId;
    }

    public LongFilter maconId() {
        if (maconId == null) {
            maconId = new LongFilter();
        }
        return maconId;
    }

    public void setMaconId(LongFilter maconId) {
        this.maconId = maconId;
    }

    public LongFilter getPrefabricantId() {
        return prefabricantId;
    }

    public LongFilter prefabricantId() {
        if (prefabricantId == null) {
            prefabricantId = new LongFilter();
        }
        return prefabricantId;
    }

    public void setPrefabricantId(LongFilter prefabricantId) {
        this.prefabricantId = prefabricantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FicheSuiviOuvrageCriteria that = (FicheSuiviOuvrageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(prjAppuis, that.prjAppuis) &&
            Objects.equals(nomBenef, that.nomBenef) &&
            Objects.equals(prenomBenef, that.prenomBenef) &&
            Objects.equals(professionBenef, that.professionBenef) &&
            Objects.equals(nbUsagers, that.nbUsagers) &&
            Objects.equals(contacts, that.contacts) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(dateRemiseDevis, that.dateRemiseDevis) &&
            Objects.equals(dateDebutTravaux, that.dateDebutTravaux) &&
            Objects.equals(dateFinTravaux, that.dateFinTravaux) &&
            Objects.equals(rue, that.rue) &&
            Objects.equals(porte, that.porte) &&
            Objects.equals(coutMenage, that.coutMenage) &&
            Objects.equals(subvOnea, that.subvOnea) &&
            Objects.equals(subvProjet, that.subvProjet) &&
            Objects.equals(autreSubv, that.autreSubv) &&
            Objects.equals(toles, that.toles) &&
            Objects.equals(animateur, that.animateur) &&
            Objects.equals(superviseur, that.superviseur) &&
            Objects.equals(controleur, that.controleur) &&
            Objects.equals(previsionId, that.previsionId) &&
            Objects.equals(natureouvrageId, that.natureouvrageId) &&
            Objects.equals(typehabitationId, that.typehabitationId) &&
            Objects.equals(sourceapprovepId, that.sourceapprovepId) &&
            Objects.equals(modeevacuationeauuseeId, that.modeevacuationeauuseeId) &&
            Objects.equals(modeevacexcretaId, that.modeevacexcretaId) &&
            Objects.equals(maconId, that.maconId) &&
            Objects.equals(prefabricantId, that.prefabricantId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            prjAppuis,
            nomBenef,
            prenomBenef,
            professionBenef,
            nbUsagers,
            contacts,
            longitude,
            latitude,
            dateRemiseDevis,
            dateDebutTravaux,
            dateFinTravaux,
            rue,
            porte,
            coutMenage,
            subvOnea,
            subvProjet,
            autreSubv,
            toles,
            animateur,
            superviseur,
            controleur,
            previsionId,
            natureouvrageId,
            typehabitationId,
            sourceapprovepId,
            modeevacuationeauuseeId,
            modeevacexcretaId,
            maconId,
            prefabricantId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FicheSuiviOuvrageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (prjAppuis != null ? "prjAppuis=" + prjAppuis + ", " : "") +
            (nomBenef != null ? "nomBenef=" + nomBenef + ", " : "") +
            (prenomBenef != null ? "prenomBenef=" + prenomBenef + ", " : "") +
            (professionBenef != null ? "professionBenef=" + professionBenef + ", " : "") +
            (nbUsagers != null ? "nbUsagers=" + nbUsagers + ", " : "") +
            (contacts != null ? "contacts=" + contacts + ", " : "") +
            (longitude != null ? "longitude=" + longitude + ", " : "") +
            (latitude != null ? "latitude=" + latitude + ", " : "") +
            (dateRemiseDevis != null ? "dateRemiseDevis=" + dateRemiseDevis + ", " : "") +
            (dateDebutTravaux != null ? "dateDebutTravaux=" + dateDebutTravaux + ", " : "") +
            (dateFinTravaux != null ? "dateFinTravaux=" + dateFinTravaux + ", " : "") +
            (rue != null ? "rue=" + rue + ", " : "") +
            (porte != null ? "porte=" + porte + ", " : "") +
            (coutMenage != null ? "coutMenage=" + coutMenage + ", " : "") +
            (subvOnea != null ? "subvOnea=" + subvOnea + ", " : "") +
            (subvProjet != null ? "subvProjet=" + subvProjet + ", " : "") +
            (autreSubv != null ? "autreSubv=" + autreSubv + ", " : "") +
            (toles != null ? "toles=" + toles + ", " : "") +
            (animateur != null ? "animateur=" + animateur + ", " : "") +
            (superviseur != null ? "superviseur=" + superviseur + ", " : "") +
            (controleur != null ? "controleur=" + controleur + ", " : "") +
            (previsionId != null ? "previsionId=" + previsionId + ", " : "") +
            (natureouvrageId != null ? "natureouvrageId=" + natureouvrageId + ", " : "") +
            (typehabitationId != null ? "typehabitationId=" + typehabitationId + ", " : "") +
            (sourceapprovepId != null ? "sourceapprovepId=" + sourceapprovepId + ", " : "") +
            (modeevacuationeauuseeId != null ? "modeevacuationeauuseeId=" + modeevacuationeauuseeId + ", " : "") +
            (modeevacexcretaId != null ? "modeevacexcretaId=" + modeevacexcretaId + ", " : "") +
            (maconId != null ? "maconId=" + maconId + ", " : "") +
            (prefabricantId != null ? "prefabricantId=" + prefabricantId + ", " : "") +
            "}";
    }
}
