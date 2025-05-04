package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.customerpagetext.CustomerPageTextDto;
import com.example.tattooplatform.dto.customerpagetext.CustomerPageTextPrioritizedDto;
import com.example.tattooplatform.model.CustomerPageText;
import com.example.tattooplatform.repos.CustomerPageTextRepo;
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
    public void reassignPrioritiesBeforeDelete(List<CustomerPageText> customerPageTexts,
                                               CustomerPageText customerPageTextToDelete) {
        customerPageTexts.remove(customerPageTextToDelete);

        if(customerPageTexts.size() > 2) {
            customerPageTexts = reassignPrioritiesInList(customerPageTexts);
        }else if(customerPageTexts.size() == 2){
            customerPageTexts.getFirst().setPriority(1);
        }

        if(!customerPageTexts.isEmpty()){
            saveListOfCustomerPageTexts(customerPageTexts);
        }

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
    public List<CustomerPageText> getCustomerPageTextListByPageSortedByAscendingPriority(String page){
        return customerPageTextRepo.findByPageOrderByPriorityAsc(page);
    }

    @Cacheable
    public CustomerPageText getCustomerPageTextByPageAndPriority(String page, int priority){
        return customerPageTextRepo.findByPageAndPriority(page, priority);
    }

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

    public List<CustomerPageText> switchPriorities(boolean decrement,
                                 CustomerPageText first, CustomerPageText second){
        if(first == null || second == null){
            return List.of();
        }

        int firstNewPriority = decrement ? -1 : 1;

        if (first.getPriority() + firstNewPriority == second.getPriority() &&
                first.getPage().equals(second.getPage())) {
            first.setPriority(first.getPriority() + firstNewPriority);
            second.setPriority(second.getPriority() - firstNewPriority);
            return List.of(first, second);
        }

        return List.of();
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
