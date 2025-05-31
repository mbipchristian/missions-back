package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.EtapeDto;
import com.missions_back.missions_back.dto.EtapeResponseDto;
import com.missions_back.missions_back.dto.FonctionResponseDto;
import com.missions_back.missions_back.dto.RessourceResponseDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.dto.UserResponseDto;
import com.missions_back.missions_back.dto.VilleResponseDto;
import com.missions_back.missions_back.model.Etape;
import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.Ressource;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.model.Ville;
import com.missions_back.missions_back.repository.EtapeRepository;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.RessourceRepo;
import com.missions_back.missions_back.repository.UserRepo;
import com.missions_back.missions_back.repository.VilleRepo;

@Service
@Transactional
public class EtapeService {
    
    @Autowired
    private EtapeRepository etapeRepository;
    
    @Autowired
    private MandatRepo mandatRepository;
    
    @Autowired
    private UserRepo userRepository;
    
    @Autowired
    private VilleRepo villeRepository;
    
    @Autowired
    private RessourceRepo ressourceRepository;
    
    public List<EtapeResponseDto> getAllEtapesByMandatId(Long mandatId) {
        List<Etape> etapes = etapeRepository.findByMandatIdOrderByOrdre(mandatId);
        return etapes.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public EtapeResponseDto getEtapeById(Long id) {
        Etape etape = etapeRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Étape non trouvée avec l'ID: " + id));
        return convertToResponseDto(etape);
    }
    
    public EtapeResponseDto createEtape(EtapeDto etapeDto) {
        validateEtapeDto(etapeDto);
        
        Mandat mandat = mandatRepository.findById(etapeDto.getMandatId())
                .orElseThrow(() -> new RuntimeException("Mandat non trouvé avec l'ID: " + etapeDto.getMandatId()));
        
        // Vérifier que les dates de l'étape sont dans l'intervalle du mandat
        validateEtapeDates(etapeDto, mandat);
        
        // Vérifier que l'ordre n'existe pas déjà
        if (etapeRepository.existsByMandatIdAndOrdre(etapeDto.getMandatId(), etapeDto.getOrdre())) {
            throw new RuntimeException("Une étape avec cet ordre existe déjà pour ce mandat");
        }
        
        Etape etape = new Etape();
        mapDtoToEntity(etapeDto, etape);
        etape.setMandat(mandat);
        
        // Associer les utilisateurs, villes et ressources
        setEtapeRelations(etapeDto, etape, mandat);
        
        Etape savedEtape = etapeRepository.save(etape);
        return convertToResponseDto(savedEtape);
    }
    
    public EtapeResponseDto updateEtape(Long id, EtapeDto etapeDto) {
        Etape existingEtape = etapeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étape non trouvée avec l'ID: " + id));
        
        validateEtapeDto(etapeDto);
        
        Mandat mandat = mandatRepository.findById(etapeDto.getMandatId())
                .orElseThrow(() -> new RuntimeException("Mandat non trouvé avec l'ID: " + etapeDto.getMandatId()));
        
        // Vérifier que les dates de l'étape sont dans l'intervalle du mandat
        validateEtapeDates(etapeDto, mandat);
        
        // Vérifier que l'ordre n'existe pas déjà (sauf pour cette étape)
        if (existingEtape.getOrdre() != etapeDto.getOrdre() && 
            etapeRepository.existsByMandatIdAndOrdre(etapeDto.getMandatId(), etapeDto.getOrdre())) {
            throw new RuntimeException("Une étape avec cet ordre existe déjà pour ce mandat");
        }
        
        mapDtoToEntity(etapeDto, existingEtape);
        existingEtape.setMandat(mandat);
        
        // Mettre à jour les relations
        setEtapeRelations(etapeDto, existingEtape, mandat);
        
        Etape updatedEtape = etapeRepository.save(existingEtape);
        return convertToResponseDto(updatedEtape);
    }
    
    public void deleteEtape(Long id) {
        Etape etape = etapeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étape non trouvée avec l'ID: " + id));
        
        etape.setActif(false);
        etape.setDeleted_at(LocalDateTime.now());
        etapeRepository.save(etape);
    }
    
    public Optional<EtapeResponseDto> getNextEtape(Long mandatId, int currentOrdre) {
        return etapeRepository.findNextEtape(mandatId, currentOrdre)
                .map(this::convertToResponseDto);
    }
    
    public Optional<EtapeResponseDto> getPreviousEtape(Long mandatId, int currentOrdre) {
        return etapeRepository.findPreviousEtape(mandatId, currentOrdre)
                .map(this::convertToResponseDto);
    }
    
    private void validateEtapeDto(EtapeDto etapeDto) {
        if (etapeDto.getDateDebut().after(etapeDto.getDateFin())) {
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }
    }
    
    private void validateEtapeDates(EtapeDto etapeDto, Mandat mandat) {
        if (etapeDto.getDateDebut().before(mandat.getDateDebut()) || 
            etapeDto.getDateFin().after(mandat.getDateFin())) {
            throw new RuntimeException("Les dates de l'étape doivent être comprises dans l'intervalle du mandat");
        }
    }
    
    private void setEtapeRelations(EtapeDto etapeDto, Etape etape, Mandat mandat) {
        // Associer les utilisateurs (uniquement ceux du mandat)
        if (etapeDto.getUserIds() != null && !etapeDto.getUserIds().isEmpty()) {
            List<User> users = userRepository.findAllById(etapeDto.getUserIds());
            // Vérifier que tous les utilisateurs sont associés au mandat
            List<Long> mandatUserIds = mandat.getUsers().stream().map(User::getId).collect(Collectors.toList());
            for (User user : users) {
                if (!mandatUserIds.contains(user.getId())) {
                    throw new RuntimeException("L'utilisateur " + user.getId() + " n'est pas associé à ce mandat");
                }
            }
            etape.setUsers(users);
        }
        
        // Associer les villes (uniquement celles du mandat)
        if (etapeDto.getVilleIds() != null && !etapeDto.getVilleIds().isEmpty()) {
            List<Ville> villes = villeRepository.findAllById(etapeDto.getVilleIds());
            List<Long> mandatVilleIds = mandat.getVilles().stream().map(Ville::getId).collect(Collectors.toList());
            for (Ville ville : villes) {
                if (!mandatVilleIds.contains(ville.getId())) {
                    throw new RuntimeException("La ville " + ville.getId() + " n'est pas associée à ce mandat");
                }
            }
            etape.setVilles(villes);
        }
        
        // Associer les ressources (uniquement celles du mandat)
        if (etapeDto.getRessourceIds() != null && !etapeDto.getRessourceIds().isEmpty()) {
            List<Ressource> ressources = ressourceRepository.findAllById(etapeDto.getRessourceIds());
            List<Long> mandatRessourceIds = mandat.getRessources().stream().map(Ressource::getId).collect(Collectors.toList());
            for (Ressource ressource : ressources) {
                if (!mandatRessourceIds.contains(ressource.getId())) {
                    throw new RuntimeException("La ressource " + ressource.getId() + " n'est pas associée à ce mandat");
                }
            }
            etape.setRessources(ressources);
        }
    }
    
    private void mapDtoToEntity(EtapeDto dto, Etape etape) {
        etape.setNom(dto.getNom());
        etape.setDateDebut(dto.getDateDebut());
        etape.setDateFin(dto.getDateFin());
        etape.setDuree(dto.getDuree());
        etape.setOrdre(dto.getOrdre());
    }
    
    private EtapeResponseDto convertToResponseDto(Etape etape) {
        // Convertir les utilisateurs
        List<UserResponseDto> userDtos = etape.getUsers() != null ? 
            etape.getUsers().stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList()) : 
            List.of();

        // Convertir les villes
        List<VilleResponseDto> villeDtos = etape.getVilles() != null ? 
            etape.getVilles().stream()
                .map(this::convertToVilleResponseDto)
                .collect(Collectors.toList()) : 
            List.of();

        // Convertir les ressources
        List<RessourceResponseDto> ressourceDtos = etape.getRessources() != null ? 
            etape.getRessources().stream()
                .map(this::convertToRessourceResponseDto)
                .collect(Collectors.toList()) : 
            List.of();

        return new EtapeResponseDto(
            etape.getId(),
            etape.getNom(),
            etape.getDateDebut(),
            etape.getDateFin(),
            etape.getDuree(),
            etape.getOrdre(),
            etape.getCreated_at(),
            etape.getUpdated_at(),
            userDtos,
            villeDtos,
            ressourceDtos
        );
    }
    // Méthodes de conversion auxiliaires
    private UserResponseDto convertToUserResponseDto(User user) {
        RoleResponseDto roleDto = user.getRole() != null ?
            new RoleResponseDto(
                user.getRole().getId(),
                user.getRole().getName(),
                user.getRole().getDescription(),
                user.getRole().getCreated_at(),
                user.getRole().getUpdated_at()
            ) : null;

        FonctionResponseDto fonctionDto = user.getFonction() != null ?
            new FonctionResponseDto(
                user.getFonction().getId(),
                user.getFonction().getNom(),
                user.getFonction().getCreated_at(),
                user.getFonction().getUpdated_at(),
                user.getFonction().getRang().getId(),
                user.getFonction().getRang().getNom(),
                user.getFonction().getRang().getCode()
            ) : null;

        return new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getMatricule(),
            user.getQuotaAnnuel(),
            roleDto,
            fonctionDto,
            user.getCreated_at(),
            user.getUpdated_at()
        );
    }

    private VilleResponseDto convertToVilleResponseDto(Ville ville) {
        return new VilleResponseDto(
            ville.getId(),
            ville.getName(),
            ville.getCode(),
            ville.isInterieur(),
            ville.getCreated_at(),
            ville.getUpdated_at()
        );
    }

    private RessourceResponseDto convertToRessourceResponseDto(Ressource ressource) {
        return new RessourceResponseDto(
            ressource.getId(),
            ressource.getName(),
            ressource.getCreated_at(),
            ressource.getUpdated_at()
        );
    }
}