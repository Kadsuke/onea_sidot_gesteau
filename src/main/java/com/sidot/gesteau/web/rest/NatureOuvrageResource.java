package com.sidot.gesteau.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.sidot.gesteau.repository.NatureOuvrageRepository;
import com.sidot.gesteau.service.NatureOuvrageQueryService;
import com.sidot.gesteau.service.NatureOuvrageService;
import com.sidot.gesteau.service.criteria.NatureOuvrageCriteria;
import com.sidot.gesteau.service.dto.NatureOuvrageDTO;
import com.sidot.gesteau.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.sidot.gesteau.domain.NatureOuvrage}.
 */
@RestController
@RequestMapping("/api")
public class NatureOuvrageResource {

    private final Logger log = LoggerFactory.getLogger(NatureOuvrageResource.class);

    private static final String ENTITY_NAME = "gesteauNatureOuvrage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NatureOuvrageService natureOuvrageService;

    private final NatureOuvrageRepository natureOuvrageRepository;

    private final NatureOuvrageQueryService natureOuvrageQueryService;

    public NatureOuvrageResource(
        NatureOuvrageService natureOuvrageService,
        NatureOuvrageRepository natureOuvrageRepository,
        NatureOuvrageQueryService natureOuvrageQueryService
    ) {
        this.natureOuvrageService = natureOuvrageService;
        this.natureOuvrageRepository = natureOuvrageRepository;
        this.natureOuvrageQueryService = natureOuvrageQueryService;
    }

    /**
     * {@code POST  /nature-ouvrages} : Create a new natureOuvrage.
     *
     * @param natureOuvrageDTO the natureOuvrageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new natureOuvrageDTO, or with status {@code 400 (Bad Request)} if the natureOuvrage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/nature-ouvrages")
    public ResponseEntity<NatureOuvrageDTO> createNatureOuvrage(@Valid @RequestBody NatureOuvrageDTO natureOuvrageDTO)
        throws URISyntaxException {
        log.debug("REST request to save NatureOuvrage : {}", natureOuvrageDTO);
        if (natureOuvrageDTO.getId() != null) {
            throw new BadRequestAlertException("A new natureOuvrage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NatureOuvrageDTO result = natureOuvrageService.save(natureOuvrageDTO);
        return ResponseEntity
            .created(new URI("/api/nature-ouvrages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /nature-ouvrages/:id} : Updates an existing natureOuvrage.
     *
     * @param id the id of the natureOuvrageDTO to save.
     * @param natureOuvrageDTO the natureOuvrageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated natureOuvrageDTO,
     * or with status {@code 400 (Bad Request)} if the natureOuvrageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the natureOuvrageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/nature-ouvrages/{id}")
    public ResponseEntity<NatureOuvrageDTO> updateNatureOuvrage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NatureOuvrageDTO natureOuvrageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update NatureOuvrage : {}, {}", id, natureOuvrageDTO);
        if (natureOuvrageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, natureOuvrageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!natureOuvrageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NatureOuvrageDTO result = natureOuvrageService.save(natureOuvrageDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, natureOuvrageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /nature-ouvrages/:id} : Partial updates given fields of an existing natureOuvrage, field will ignore if it is null
     *
     * @param id the id of the natureOuvrageDTO to save.
     * @param natureOuvrageDTO the natureOuvrageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated natureOuvrageDTO,
     * or with status {@code 400 (Bad Request)} if the natureOuvrageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the natureOuvrageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the natureOuvrageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/nature-ouvrages/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<NatureOuvrageDTO> partialUpdateNatureOuvrage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NatureOuvrageDTO natureOuvrageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update NatureOuvrage partially : {}, {}", id, natureOuvrageDTO);
        if (natureOuvrageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, natureOuvrageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!natureOuvrageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NatureOuvrageDTO> result = natureOuvrageService.partialUpdate(natureOuvrageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, natureOuvrageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /nature-ouvrages} : get all the natureOuvrages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of natureOuvrages in body.
     */
    @GetMapping("/nature-ouvrages")
    public ResponseEntity<List<NatureOuvrageDTO>> getAllNatureOuvrages(NatureOuvrageCriteria criteria, Pageable pageable) {
        log.debug("REST request to get NatureOuvrages by criteria: {}", criteria);
        Page<NatureOuvrageDTO> page = natureOuvrageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /nature-ouvrages/count} : count all the natureOuvrages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/nature-ouvrages/count")
    public ResponseEntity<Long> countNatureOuvrages(NatureOuvrageCriteria criteria) {
        log.debug("REST request to count NatureOuvrages by criteria: {}", criteria);
        return ResponseEntity.ok().body(natureOuvrageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /nature-ouvrages/:id} : get the "id" natureOuvrage.
     *
     * @param id the id of the natureOuvrageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the natureOuvrageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/nature-ouvrages/{id}")
    public ResponseEntity<NatureOuvrageDTO> getNatureOuvrage(@PathVariable Long id) {
        log.debug("REST request to get NatureOuvrage : {}", id);
        Optional<NatureOuvrageDTO> natureOuvrageDTO = natureOuvrageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(natureOuvrageDTO);
    }

    /**
     * {@code DELETE  /nature-ouvrages/:id} : delete the "id" natureOuvrage.
     *
     * @param id the id of the natureOuvrageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/nature-ouvrages/{id}")
    public ResponseEntity<Void> deleteNatureOuvrage(@PathVariable Long id) {
        log.debug("REST request to delete NatureOuvrage : {}", id);
        natureOuvrageService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/nature-ouvrages?query=:query} : search for the natureOuvrage corresponding
     * to the query.
     *
     * @param query the query of the natureOuvrage search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/nature-ouvrages")
    public ResponseEntity<List<NatureOuvrageDTO>> searchNatureOuvrages(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of NatureOuvrages for query {}", query);
        Page<NatureOuvrageDTO> page = natureOuvrageService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
