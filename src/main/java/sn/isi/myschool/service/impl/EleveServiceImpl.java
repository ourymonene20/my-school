package sn.isi.myschool.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.myschool.domain.Eleve;
import sn.isi.myschool.repository.EleveRepository;
import sn.isi.myschool.service.EleveService;

/**
 * Service Implementation for managing {@link Eleve}.
 */
@Service
@Transactional
public class EleveServiceImpl implements EleveService {

    private final Logger log = LoggerFactory.getLogger(EleveServiceImpl.class);

    private final EleveRepository eleveRepository;

    public EleveServiceImpl(EleveRepository eleveRepository) {
        this.eleveRepository = eleveRepository;
    }

    @Override
    public Eleve save(Eleve eleve) {
        log.debug("Request to save Eleve : {}", eleve);
        return eleveRepository.save(eleve);
    }

    @Override
    public Optional<Eleve> partialUpdate(Eleve eleve) {
        log.debug("Request to partially update Eleve : {}", eleve);

        return eleveRepository
            .findById(eleve.getId())
            .map(existingEleve -> {
                if (eleve.getNom() != null) {
                    existingEleve.setNom(eleve.getNom());
                }
                if (eleve.getPrenom() != null) {
                    existingEleve.setPrenom(eleve.getPrenom());
                }
                if (eleve.getEmail() != null) {
                    existingEleve.setEmail(eleve.getEmail());
                }
                if (eleve.getDateNaiss() != null) {
                    existingEleve.setDateNaiss(eleve.getDateNaiss());
                }

                return existingEleve;
            })
            .map(eleveRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Eleve> findAll(Pageable pageable) {
        log.debug("Request to get all Eleves");
        return eleveRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Eleve> findOne(Long id) {
        log.debug("Request to get Eleve : {}", id);
        return eleveRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Eleve : {}", id);
        eleveRepository.deleteById(id);
    }
}
