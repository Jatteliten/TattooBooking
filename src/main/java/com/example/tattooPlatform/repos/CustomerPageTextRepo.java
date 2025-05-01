package com.example.tattooPlatform.repos;

import com.example.tattooPlatform.model.CustomerPageText;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerPageTextRepo extends JpaRepository<CustomerPageText, UUID> {
    CustomerPageText findTopByPageAndSectionOrderByCreatedDesc(String page, String section);
    List<CustomerPageText> findByPage(String page);
    List<CustomerPageText> findByPageOrderByPriorityAsc(String page);
    CustomerPageText findByPageAndPriority(String page, int priority);
    int countByPage(String page);
}
