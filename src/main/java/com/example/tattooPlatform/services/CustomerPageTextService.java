package com.example.tattooPlatform.services;

import com.example.tattooPlatform.dto.customerpagetext.CustomerPageTextDto;
import com.example.tattooPlatform.dto.customerpagetext.CustomerPageTextPrioritizedDto;
import com.example.tattooPlatform.model.CustomerPageText;
import com.example.tattooPlatform.repos.CustomerPageTextRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CacheConfig;

@Service
@CacheConfig(cacheNames = "customerPageTexts")
public class CustomerPageTextService {

    private final CustomerPageTextRepo customerPageTextRepo;

    public CustomerPageTextService(CustomerPageTextRepo customerPageTextRepo){
        this.customerPageTextRepo = customerPageTextRepo;
    }

    @CacheEvict(allEntries = true)
    public void saveCustomerPageText(CustomerPageText customerPageText){
        customerPageText.setCreated(LocalDateTime.now());
        customerPageTextRepo.save(customerPageText);
    }

    @CacheEvict(allEntries = true)
    public void saveListOfCustomerPageTexts(List<CustomerPageText> customerPageTexts){
        customerPageTextRepo.saveAll(customerPageTexts);
    }

    @CacheEvict(allEntries = true)
    public void deleteCustomerPageText(CustomerPageText customerPageText){
        customerPageTextRepo.delete(customerPageText);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void reassignPrioritiesOnDelete(
            List<CustomerPageText> customerPageTexts, CustomerPageText customerPageTextToDelete) {
        customerPageTexts.remove(customerPageTextToDelete);
        customerPageTexts = reassignPrioritiesInList(customerPageTexts);
        saveListOfCustomerPageTexts(customerPageTexts);
        deleteCustomerPageText(customerPageTextToDelete);
    }

    @Cacheable
    public CustomerPageText getCustomerPageTextById(UUID id){
        return customerPageTextRepo.findById(id).orElse(null);
    }

    @Cacheable
    public CustomerPageText getLatestCustomerPageTextByPageAndSection(String page, String section){
        return customerPageTextRepo.findTopByPageAndSectionOrderByCreatedDesc(page, section);
    }

    @Cacheable
    public List<CustomerPageText> getCustomerPageTextListByPage(String page){
        return customerPageTextRepo.findByPage(page);
    }

    @Cacheable
    public List<CustomerPageText> getCustomerPageTextListByPageSortedByPriority(String page){
        return customerPageTextRepo.findByPageOrderByPriorityAsc(page);
    }

    @Cacheable
    public CustomerPageText getCustomerPageTextByPageAndPriority(String page, int priority){
        return customerPageTextRepo.findByPageAndPriority(page, priority);
    }

    @Cacheable
    public int countCustomerPageTextsByPage(String page){
        return customerPageTextRepo.countByPage(page);
    }

    public CustomerPageTextDto convertCustomerPageTextToCustomerPageTextDto(CustomerPageText customerPageText){
        return CustomerPageTextDto.builder()
                .text(customerPageText.getText())
                .section(customerPageText.getSection())
                .build();
    }

    public CustomerPageTextPrioritizedDto convertCustomerPageTextToCustomerPageTextPrioritizedDto(
            CustomerPageText customerPageText){
        return CustomerPageTextPrioritizedDto.builder()
                .text(customerPageText.getText())
                .section(customerPageText.getSection())
                .priority(customerPageText.getPriority())
                .build();
    }

    public List<CustomerPageTextPrioritizedDto> convertCustomerPageTextListToCustomerPageTextPrioritizedDtoList(
            List<CustomerPageText> customerPageTexts){
        return customerPageTexts.stream().map(this::convertCustomerPageTextToCustomerPageTextPrioritizedDto).toList();
    }

    public List<CustomerPageText> reassignPrioritiesInList(List<CustomerPageText> customerPageTexts){
        for(int i = 0; i < customerPageTexts.size(); i++){
            if(customerPageTexts.get(i).getPriority() != i + 1){
                customerPageTexts.get(i).setPriority(i + 1);
            }
        }

        return customerPageTexts;
    }
}
