package com.azienda.erp.erp_backend.service;

import com.azienda.erp.erp_backend.entity.User;
import com.azienda.erp.erp_backend.exception.UserNotFoundException;
import com.azienda.erp.erp_backend.exception.UsernameAlreadyExistsException;
import com.azienda.erp.erp_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Verifica se l'utente di default 'admin' è presente nel sistema.
     * <p>
     * Questo metodo verifica se l'utente con username 'admin' esiste nel database.
     * <p>
     * @return True se l'utente di default esiste, False altrimenti.
     */
    public boolean isDefaultUserPresent() {
        return userRepository.findByUsername("admin") != null;
    }

    /**
     * Salva un nuovo utente nel sistema con la password codificata.
     * <p>
     * Questo metodo prende un oggetto User, codifica la sua password
     * per motivi di sicurezza e lo salva nel database.
     * <p>
     * @param user L'oggetto User da salvare, contenente i dettagli dell'utente.
     * @return L'oggetto User salvato, con le informazioni aggiornate dal database.
     */
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Registra un nuovo utente nel sistema.
     * <p>
     * Questo metodo prende un oggetto User e lo salva nel database
     * se l'username non esiste già. Se l'utente che si vuole
     * creare ha il ruolo di amministratore, viene cancellato
     * l'utente di default 'admin' se presente.
     * <p>
     * @param user L'oggetto User da salvare, contenente i dettagli dell'utente.
     * @return L'oggetto User salvato, con le informazioni aggiornate dal database.
     * @throws UsernameAlreadyExistsException Se l'username specificato è già presente nel sistema.
     */
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("Errore: Username già esistente!");
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            deleteDefaultUser();
        }

        return saveUser(user);
    }

    /**
     * Elimina l'utente di default "admin" dal database.
     * <p>
     * Questo metodo utilizza il repository {@link UserRepository} per cercare l'utente
     * con username "admin" e, se esiste, lo elimina dal database.
     */
    public void deleteDefaultUser() {
        User defaultUser = userRepository.findByUsername("admin");
        if (defaultUser != null) {
            userRepository.delete(defaultUser);
        }
    }

    /**
     * Elimina l'utente con l'ID specificato.
     * <p>
     * Questo metodo utilizza il repository {@link UserRepository} per cercare l'utente
     * con l'ID specificato e, se esiste, lo elimina dal database.
     * <p>
     * @param id L'ID dell'utente da eliminare.
     * @throws UserNotFoundException Se l'utente non esiste.
     */
    public void deleteUserById(Long id) {
        User user = findById(id);  // Solleva l'eccezione se l'utente non esiste
        userRepository.delete(user);
    }

    /**
     * Cerca un utente con l'ID specificato.
     * <p>
     * Questo metodo utilizza il repository {@link UserRepository} per cercare un utente
     * con l'ID specificato e, se esiste, lo restituisce.
     * <p>
     * @param id L'ID dell'utente da cercare.
     * @return L'oggetto User trovato.
     * @throws UserNotFoundException Se l'utente non esiste.
     */
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Utente non trovato con ID: " + id));
    }
}
