package com.elit.outildedecision.shared.user.event;

/**
 * Domain event published once a new user account <em>and</em> its confirmation token have been
 * persisted.
 *
 * <p>Carries the persisted user identifier and the freshly created confirmation token so the
 * registration email can be sent <em>after</em> the transaction commits (see the matching
 * {@code @TransactionalEventListener}), keeping the slow mail polling — and any DB write — out of
 * the account-creation transaction.
 *
 * @param userId            the identifier of the freshly registered user
 * @param confirmationToken the confirmation token persisted with the account
 */
public record UserRegisteredEvent(Long userId, String confirmationToken) {
}
