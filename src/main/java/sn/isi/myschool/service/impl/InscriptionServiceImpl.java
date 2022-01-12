package sn.isi.myschool.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.myschool.domain.Inscription;
import sn.isi.myschool.repository.InscriptionRepository;
import sn.isi.myschool.service.InscriptionService;

/**
 * Service Implementation for managing {@link Inscription}.
 */
@Service
@Transactional
public class InscriptionServiceImpl implements InscriptionService {

    private final Logger log = LoggerFactory.getLogger(InscriptionServiceImpl.class);

    private final InscriptionRepository inscriptionRepository;

    public InscriptionServiceImpl(InscriptionRepository inscriptionRepository) {
        this.inscriptionRepository = inscriptionRepository;
    }

    @Override
    public Inscription save(Inscription inscription) {
        log.debug("Request to save Inscription : {}", inscription);
        return inscriptionRepository.save(inscription);
    }

    @Override
    public Optional<Inscription> partialUpdate(Inscription inscription) {
        log.debug("Request to partially update Inscription : {}", inscription);

        return inscriptionRepository
            .findById(inscription.getId())
            .map(existingInscription -> {
                if (inscription.getDateInscprion() != null) {
                    existingInscription.setDateInscprion(inscription.getDateInscprion());
                }
                if (inscription.getAnneeScolaire() != null) {
                    existingInscription.setAnneeScolaire(inscription.getAnneeScolaire());
                }

                return existingInscription;
            })
            .map(inscriptionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Inscription> findAll(Pageable pageable) {
        log.debug("Request to get all Inscriptions");
        return inscriptionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inscription> findOne(Long id) {
        log.debug("Request to get Inscription : {}", id);
        return inscriptionRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Inscription : {}", id);
        inscriptionRepository.deleteById(id);
    }
}
