package br.com.connect.service;

import br.com.connect.exception.EmptyFileException;
import br.com.connect.exception.ImportTenantsException;
import br.com.connect.model.Condominium;
import br.com.connect.model.Tenant;
import br.com.connect.model.transport.condominium.tenant.CreateTenantDTO;
import br.com.connect.repository.TenantRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TenantService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantService.class);

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<CreateTenantDTO> convertTenants(MultipartFile tenantsAsCsv) throws EmptyFileException, ImportTenantsException {
        LOGGER.info("Starting tenant CSV processing");
        if (tenantsAsCsv.isEmpty()) {
            throw new EmptyFileException("Registration must contain at least one tenant");
        }
        try (Reader reader = new BufferedReader(new InputStreamReader(tenantsAsCsv.getInputStream()))) {
            CsvToBean<CreateTenantDTO> csvToBean = new CsvToBeanBuilder<CreateTenantDTO>(reader).withType(CreateTenantDTO.class).build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new ImportTenantsException(e.getMessage());
        }
    }

    public List<Tenant> listCurrentTenants(Condominium condominium) {
        return this.tenantRepository.findByCondominium(condominium).orElse(new ArrayList<>());
    }

    @Transactional
    public void deleteAll(List<Tenant> tenants) {
        this.tenantRepository.deleteAll(tenants);
    }

    @Transactional
    public void createAll(Set<Tenant> tenants) {
        this.tenantRepository.saveAll(tenants);
    }
}
