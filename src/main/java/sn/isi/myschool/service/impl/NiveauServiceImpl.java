package sn.isi.myschool.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.myschool.domain.Niveau;
import sn.isi.myschool.repository.NiveauRepository;
import sn.isi.myschool.service.NiveauService;

/**
 * Service Implementation for managing {@link Niveau}.
 */
@Service
@Transactional
public class NiveauServiceImpl implements NiveauService {

    private final Logger log = LoggerFactory.getLogger(NiveauServiceImpl.class);

    private final NiveauRepository niveauRepository;

    public NiveauServiceImpl(NiveauRepository niveauRepository) {
        this.niveauRepository = niveauRepository;
    }

    @Override
    public Niveau save(Niveau niveau) {
        log.debug("Request to save Niveau : {}", niveau);
        return niveauRepository.save(niveau);
    }

    @Override
    public Optional<Niveau> partialUpdate(Niveau niveau) {
        log.debug("Request to partially update Niveau : {}", niveau);

        return niveauRepository
            .findById(niveau.getId())
            .map(existingNiveau -> {
                if (niveau.getNom() != null) {
                    existingNiveau.setNom(niveau.getNom());
                }
                if (niveau.getLibelle() != null) {
                    existingNiveau.setLibelle(niveau.getLibelle());
                }

                return existingNiveau;
            })
            .map(niveauRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Niveau> findAll(Pageable pageable) {
        log.debug("Request to get all Niveaus");
        return niveauRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Niveau> findOne(Long id) {
        log.debug("Request to get Niveau : {}", id);
        return niveauRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Niveau : {}", id);
        niveauRepository.deleteById(id);
    }
}
