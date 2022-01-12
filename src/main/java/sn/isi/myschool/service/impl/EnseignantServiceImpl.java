package sn.isi.myschool.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.myschool.domain.Enseignant;
import sn.isi.myschool.repository.EnseignantRepository;
import sn.isi.myschool.service.EnseignantService;

/**
 * Service Implementation for managing {@link Enseignant}.
 */
@Service
@Transactional
public class EnseignantServiceImpl implements EnseignantService {

    private final Logger log = LoggerFactory.getLogger(EnseignantServiceImpl.class);

    private final EnseignantRepository enseignantRepository;

    public EnseignantServiceImpl(EnseignantRepository enseignantRepository) {
        this.enseignantRepository = enseignantRepository;
    }

    @Override
    public Enseignant save(Enseignant enseignant) {
        log.debug("Request to save Enseignant : {}", enseignant);
        return enseignantRepository.save(enseignant);
    }

    @Override
    public Optional<Enseignant> partialUpdate(Enseignant enseignant) {
        log.debug("Request to partially update Enseignant : {}", enseignant);

        return enseignantRepository
            .findById(enseignant.getId())
            .map(existingEnseignant -> {
                if (enseignant.getNom() != null) {
                    existingEnseignant.setNom(enseignant.getNom());
                }
                if (enseignant.getPrenom() != null) {
                    existingEnseignant.setPrenom(enseignant.getPrenom());
                }
                if (enseignant.getEmail() != null) {
                    existingEnseignant.setEmail(enseignant.getEmail());
                }
                if (enseignant.getDateNaiss() != null) {
                    existingEnseignant.setDateNaiss(enseignant.getDateNaiss());
                }

                return existingEnseignant;
            })
            .map(enseignantRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Enseignant> findAll(Pageable pageable) {
        log.debug("Request to get all Enseignants");
        return enseignantRepository.findAll(pageable);
    }

    public Page<Enseignant> findAllWithEagerRelationships(Pageable pageable) {
        return enseignantRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Enseignant> findOne(Long id) {
        log.debug("Request to get Enseignant : {}", id);
        return enseignantRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Enseignant : {}", id);
        enseignantRepository.deleteById(id);
    }
}
