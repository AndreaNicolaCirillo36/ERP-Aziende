package com.azienda.erp.erp_backend.repository;

import com.azienda.erp.erp_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Cerca un utente in base al suo username.
     *
     * @param username Lo username dell'utente da cercare.
     * @return L'oggetto User associato allo username specificato, o null se non viene trovato alcun utente.
     */
    User findByUsername(String username);
}
