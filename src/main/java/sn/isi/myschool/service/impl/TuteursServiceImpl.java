package sn.isi.myschool.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.myschool.domain.Tuteurs;
import sn.isi.myschool.repository.TuteursRepository;
import sn.isi.myschool.service.TuteursService;

/**
 * Service Implementation for managing {@link Tuteurs}.
 */
@Service
@Transactional
public class TuteursServiceImpl implements TuteursService {

    private final Logger log = LoggerFactory.getLogger(TuteursServiceImpl.class);

    private final TuteursRepository tuteursRepository;

    public TuteursServiceImpl(TuteursRepository tuteursRepository) {
        this.tuteursRepository = tuteursRepository;
    }

    @Override
    public Tuteurs save(Tuteurs tuteurs) {
        log.debug("Request to save Tuteurs : {}", tuteurs);
        return tuteursRepository.save(tuteurs);
    }

    @Override
    public Optional<Tuteurs> partialUpdate(Tuteurs tuteurs) {
        log.debug("Request to partially update Tuteurs : {}", tuteurs);

        return tuteursRepository
            .findById(tuteurs.getId())
            .map(existingTuteurs -> {
                if (tuteurs.getNom() != null) {
                    existingTuteurs.setNom(tuteurs.getNom());
                }
                if (tuteurs.getPrenom() != null) {
                    existingTuteurs.setPrenom(tuteurs.getPrenom());
                }
                if (tuteurs.getTelephone() != null) {
                    existingTuteurs.setTelephone(tuteurs.getTelephone());
                }
                if (tuteurs.getEmail() != null) {
                    existingTuteurs.setEmail(tuteurs.getEmail());
                }

                return existingTuteurs;
            })
            .map(tuteursRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Tuteurs> findAll(Pageable pageable) {
        log.debug("Request to get all Tuteurs");
        return tuteursRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tuteurs> findOne(Long id) {
        log.debug("Request to get Tuteurs : {}", id);
        return tuteursRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tuteurs : {}", id);
        tuteursRepository.deleteById(id);
    }
}
