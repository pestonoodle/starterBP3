package com.progettiportfoglio.multichat.auth;

import com.progettiportfoglio.multichat.security.AuthenticatedUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    // TODO [AUTH-01]: iniettare AuthTokenRepository tramite costruttore.
    // Il repository e le entita User/AuthToken devono essere creati dallo studente.

    public String createToken(Long userId) {
        String token = UUID.randomUUID().toString();

        // TODO [AUTH-02]: creare AuthToken, associare l'utente e salvare nel DB.
        // Restituire token soltanto dopo il salvataggio.
        // Sostituire questa eccezione con il codice e con: return token;
        throw new UnsupportedOperationException("TODO: salvare il token nel database");
    }

    public Optional<AuthenticatedUser> findUserByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        // TODO [AUTH-03]: cercare il valore esatto nella tabella auth_tokens.
        // Se presente, recuperare l'utente e restituire:
        // Optional.of(new AuthenticatedUser(user.getId(), user.getUsername())).
        // Se assente, restituire Optional.empty().
        // Nessun token viene accettato finche questo metodo non e completato.
        return Optional.empty();
    }

    public void deleteToken(String token) {
        // TODO [AUTH-04]: eliminare dal DB soltanto il token ricevuto.
        // Sostituire questa eccezione con l'operazione del repository.
        throw new UnsupportedOperationException("TODO: eliminare il token dal database");
    }
}
