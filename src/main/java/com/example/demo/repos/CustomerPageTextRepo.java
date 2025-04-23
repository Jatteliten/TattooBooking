package com.example.demo.repos;

import com.example.demo.model.CustomerPageText;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerPageTextRepo extends JpaRepository<CustomerPageText, UUID> {
    CustomerPageText findTopByPageAndSectionOrderByCreatedDesc(String page, String section);
    List<CustomerPageText> findByPage(String page);
}
