package com.elit.outildedecision.ai.kiviat.dto;

import com.elit.outildedecision.shared.util.MethodologyType;

/**
 * Internal computation carrier holding the five Kiviat axis scores plus the methodology id.
 *
 * <p>Both service flows (AI methodology reference values and project answer averages) produce a
 * {@code KiviatScores}; {@link com.elit.outildedecision.ai.kiviat.mapper.KiviatMapper} converts it
 * to the wire {@link KiviatResponse}, keeping the response assembly out of the service.
 *
 * @param id methodology recommended by the AI (null for the project-answer flow)
 * @param flexibiliteGestion management flexibility axis
 * @param flexibiliteSolution solution flexibility axis
 * @param confianceEtDelegation trust and delegation axis
 * @param maitrisePerimetre scope control axis
 * @param coordination coordination axis
 */
public record KiviatScores(
    MethodologyType id,
    double flexibiliteGestion,
    double flexibiliteSolution,
    double confianceEtDelegation,
    double maitrisePerimetre,
    double coordination) {
}
