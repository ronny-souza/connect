package br.com.connect.service;

import br.com.connect.exception.*;
import br.com.connect.model.Condominium;
import br.com.connect.model.Tenant;
import br.com.connect.model.User;
import br.com.connect.model.enums.MailTypeEnum;
import br.com.connect.model.transport.condominium.CondominiumDTO;
import br.com.connect.model.transport.condominium.CreateCondominiumDTO;
import br.com.connect.model.transport.condominium.projection.AvailableCondominiumDTO;
import br.com.connect.model.transport.condominium.tenant.CreateTenantDTO;
import br.com.connect.model.transport.search.ListSearchCriteriaDTO;
import br.com.connect.model.transport.user.ConfirmEmailDTO;
import br.com.connect.model.transport.user.UserDTO;
import br.com.connect.repository.CondominiumRepository;
import br.com.connect.specification.CondominiumSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CondominiumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CondominiumService.class);

    private final CondominiumRepository condominiumRepository;
    private final UserService userService;
    private final IdentityConfirmationService identityConfirmationService;
    private final MailService mailService;
    private final TenantService tenantService;

    public CondominiumService(CondominiumRepository condominiumRepository, UserService userService, IdentityConfirmationService identityConfirmationService, MailService mailService, TenantService tenantService) {
        this.condominiumRepository = condominiumRepository;
        this.userService = userService;
        this.identityConfirmationService = identityConfirmationService;
        this.mailService = mailService;
        this.tenantService = tenantService;
    }

    private Condominium findByConnectIdentifierAndUser(String condominiumUUID, UserDTO userInSession) throws CondominiumNotFoundException {
        Optional<Condominium> optionalCondominium = this.condominiumRepository.findByConnectIdentifierAndUserConnectIdentifierAndUserEnabledTrue(condominiumUUID, userInSession.connectIdentifier());

        if (optionalCondominium.isEmpty()) {
            throw new CondominiumNotFoundException("The condominium was not found or the user is not the owner");
        }

        return optionalCondominium.get();
    }

    @Transactional
    public CondominiumDTO create(CreateCondominiumDTO createCondominiumDTO, UserDTO userInSession) throws UserNotFoundException, CondominiumEmailAlreadyRegisteredException {
        LOGGER.info("Starting condominium registration...");

        User user = this.userService.findByEmail(userInSession.email());
        Condominium condominium = new Condominium(createCondominiumDTO, user);
        if (createCondominiumDTO.email() != null) {

            if (condominiumRepository.existsByEmail(createCondominiumDTO.email())) {
                throw new CondominiumEmailAlreadyRegisteredException(String.format("The email %s already belongs to a registered condominium", createCondominiumDTO.email()));
            }

            this.publishConfirmationEmailMessage(createCondominiumDTO.name(), createCondominiumDTO.email(), user);
        }

        this.condominiumRepository.save(condominium);


        return new CondominiumDTO(condominium);
    }

    @Transactional
    public void confirmEmail(ConfirmEmailDTO confirmEmailDTO) throws ConfirmationCodeExpiredException, CondominiumNotFoundException {
        this.identityConfirmationService.confirmEmail(confirmEmailDTO);

        Optional<Condominium> optionalCondominium = this.condominiumRepository.findByEmail(confirmEmailDTO.email());
        if (optionalCondominium.isEmpty()) {
            throw new CondominiumNotFoundException(String.format("Condominium with email: %s is not found", confirmEmailDTO.email()));
        }
        Condominium condominium = optionalCondominium.get();
        condominium.activateEmail();
        this.condominiumRepository.save(condominium);
    }

    public void regenerateConfirmationCode(String email, UserDTO userInSession) throws CondominiumNotFoundException {
        Optional<Condominium> optionalCondominium = this.condominiumRepository.findByEmailAndUserConnectIdentifier(email, userInSession.connectIdentifier());
        if (optionalCondominium.isEmpty()) {
            throw new CondominiumNotFoundException("Condominium was not found or user is not the owner");
        }

        Condominium condominium = optionalCondominium.get();
        this.identityConfirmationService.deleteCodeIfExists(email);
        LOGGER.info("Generating new confirmation code...");
        this.publishConfirmationEmailMessage(condominium.getName(), email, condominium.getUser());
    }

    @Transactional
    public void registerTenants(MultipartFile tenantsAsCsv, String condominiumUUID, UserDTO userInSession) throws CondominiumNotFoundException, EmptyFileException, ImportTenantsException {
        Condominium condominium = this.findByConnectIdentifierAndUser(condominiumUUID, userInSession);

        List<CreateTenantDTO> tenantsToCreate = this.tenantService.convertTenants(tenantsAsCsv);
        Set<Tenant> newTenants = tenantsToCreate.stream().map(newTenant -> new Tenant(newTenant, condominium)).collect(Collectors.toSet());

        List<Tenant> currentTenants = this.tenantService.listCurrentTenants(condominium);
        List<Tenant> tenantsToDelete = new ArrayList<>();

        for (Tenant currentTenant : currentTenants) {
            Optional<Tenant> optionalTenant = newTenants.stream().filter(newTenant -> newTenant.getEmail().equals(currentTenant.getEmail())).findAny();
            if (optionalTenant.isPresent()) {
                tenantsToDelete.add(currentTenant);
            }
        }

        LOGGER.info("Removing repeat tenants...");
        this.tenantService.deleteAll(tenantsToDelete);

        LOGGER.info("Registering new tenants...");
        condominium.getTenants().addAll(newTenants);
    }

    public Page<AvailableCondominiumDTO> listAvailableCondominiums(Pageable pageable, ListSearchCriteriaDTO searchParams, UserDTO userInSession) {
        Specification<Condominium> specifications = new CondominiumSpecification(CondominiumSpecification.availableForUser(userInSession.email())).withSearchParams(searchParams).build();
        return this.condominiumRepository.findAll(specifications, AvailableCondominiumDTO.class, pageable);
    }

    private void publishConfirmationEmailMessage(String condominiumName, String email, User user) {
        String subject = "Connect - Confirmação de e-mail do condomínio";
        MailTypeEnum type = MailTypeEnum.CONFIRM_CONDOMINIUM_EMAIL;

        String code = this.identityConfirmationService.createConfirmationCode(email, user);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getName());
        properties.put("code", code);
        properties.put("condominium", condominiumName);
        this.mailService.buildAndpublish(null, email, subject, type, properties);
    }
}
