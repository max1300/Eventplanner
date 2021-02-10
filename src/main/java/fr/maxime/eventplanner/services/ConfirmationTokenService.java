package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.models.ConfirmationToken;
import fr.maxime.eventplanner.repositories.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    public static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);
    public static final String TOKEN_NOT_FOUND = "ConfirmationToken non trouvé";

    private final Javers javers;
    private final ConfirmationTokenRepository repository;

    @Transactional
    public ConfirmationToken save(ConfirmationToken token) {
        return repository.save(token);
    }

    public ConfirmationToken createConfirmationToken(AppUser savedItem) {
        String token = UUID.randomUUID().toString();
        return new ConfirmationToken(
                token,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                savedItem
        );
    }

    public ConfirmationToken getByToken(String token) throws IllegalStateException{
        return repository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException(TOKEN_NOT_FOUND));
    }

    public boolean checkExpirationToken(ConfirmationToken confirmationToken) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = confirmationToken.getExpiredAt();
        return now.isAfter(expiredAt);
    }

    @Transactional
    public ConfirmationToken update(Long confirmationTokenId, ConfirmationToken byToken) {
        Optional<ConfirmationToken> existingItemOptional = repository.findById(confirmationTokenId);

        if (existingItemOptional.isPresent()) {
            ConfirmationToken existingItem = existingItemOptional.get();
            Diff diff = javers.compare(existingItem, byToken);

            BeanUtils.copyProperties(byToken, existingItem);
            existingItem.setConfirmationTokenId(confirmationTokenId);

            LOG.info("Mise à jour de l'appuser {}. MOdification des propriétées : {}", confirmationTokenId, diff.getChanges());
            return existingItem;
        } else {
            throw new IllegalStateException(TOKEN_NOT_FOUND);
        }
    }
}
