package com.elit.outildedecision.shared.auth.service;

import java.util.List;

/**
 * Identity carried by a verified JWT, exposed without leaking the JWT library.
 *
 * @param subject the token subject (the user e-mail)
 * @param roles   the roles granted by the token (empty for a refresh token)
 */
public record AuthenticatedToken(String subject, List<String> roles) {
}
