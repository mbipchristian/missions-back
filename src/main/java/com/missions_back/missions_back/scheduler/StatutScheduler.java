package com.missions_back.missions_back.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.missions_back.missions_back.service.MandatService;
import com.missions_back.missions_back.service.OrdreMissionService;

@Component
public class StatutScheduler {
    
    private final MandatService mandatService;
    private final OrdreMissionService ordreMissionService;

    public StatutScheduler(MandatService mandatService, OrdreMissionService ordreMissionService) {
        this.mandatService = mandatService;
        this.ordreMissionService = ordreMissionService;
    }

    // Exécution toutes les heures pour mettre à jour les statuts
    @Scheduled(cron = "0 */1 * * * *")   // Toutes les 1 minute
    public void mettreAJourStatutsAutomatiquement() {
        try {
            mandatService.mettreAJourStatutsAutomatiquement();
            ordreMissionService.mettreAJourStatutsAutomatiquement();
        } catch (Exception e) {
            // Logger l'erreur
            System.err.println("Erreur lors de la mise à jour automatique des statuts: " + e.getMessage());
        }
    }
}
