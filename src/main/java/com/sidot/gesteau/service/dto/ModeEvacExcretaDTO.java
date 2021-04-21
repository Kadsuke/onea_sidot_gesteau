package com.sidot.gesteau.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.sidot.gesteau.domain.ModeEvacExcreta} entity.
 */
public class ModeEvacExcretaDTO implements Serializable {

    private Long id;

    @NotNull
    private String libelle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModeEvacExcretaDTO)) {
            return false;
        }

        ModeEvacExcretaDTO modeEvacExcretaDTO = (ModeEvacExcretaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, modeEvacExcretaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ModeEvacExcretaDTO{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
