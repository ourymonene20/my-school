package sn.isi.myschool.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.myschool.domain.Salle;
import sn.isi.myschool.repository.SalleRepository;
import sn.isi.myschool.service.SalleService;

/**
 * Service Implementation for managing {@link Salle}.
 */
@Service
@Transactional
public class SalleServiceImpl implements SalleService {

    private final Logger log = LoggerFactory.getLogger(SalleServiceImpl.class);

    private final SalleRepository salleRepository;

    public SalleServiceImpl(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    @Override
    public Salle save(Salle salle) {
        log.debug("Request to save Salle : {}", salle);
        return salleRepository.save(salle);
    }

    @Override
    public Optional<Salle> partialUpdate(Salle salle) {
        log.debug("Request to partially update Salle : {}", salle);

        return salleRepository
            .findById(salle.getId())
            .map(existingSalle -> {
                if (salle.getLibelle() != null) {
                    existingSalle.setLibelle(salle.getLibelle());
                }
                if (salle.getCapacite() != null) {
                    existingSalle.setCapacite(salle.getCapacite());
                }

                return existingSalle;
            })
            .map(salleRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Salle> findAll(Pageable pageable) {
        log.debug("Request to get all Salles");
        return salleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Salle> findOne(Long id) {
        log.debug("Request to get Salle : {}", id);
        return salleRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Salle : {}", id);
        salleRepository.deleteById(id);
    }
}
