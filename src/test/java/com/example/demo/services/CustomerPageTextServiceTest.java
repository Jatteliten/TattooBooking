package com.example.demo.services;

import com.example.demo.model.CustomerPageText;
import com.example.demo.repos.CustomerPageTextRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class CustomerPageTextServiceTest {
    @Autowired
    private CustomerPageTextRepo customerPageTextRepo;
    @Autowired
    private CustomerPageTextService customerPageTextService;
    private static final String TEST_STRING = "test";

    @AfterEach
    void deleteAll(){
        customerPageTextRepo.deleteAll();
    }

    @Test
    void getCustomerPageTextById_shouldGetCorrectCustomerPageText() {
        CustomerPageText customerPageText = new CustomerPageText();
        customerPageTextRepo.save(customerPageText);

        assertEquals(customerPageText, customerPageTextService.getCustomerPageTextById(customerPageText.getId()));
    }

    @Test
    void saveCustomerPageText_shouldSaveCorrectly() {
        customerPageTextService.saveCustomerPageText(new CustomerPageText());

        assertEquals(1, customerPageTextRepo.findAll().size());
    }

    @Test
    void deleteCustomerPageText_shouldDeleteCorrectly() {
        CustomerPageText customerPageText = new CustomerPageText();
        customerPageTextService.saveCustomerPageText(customerPageText);

        assertEquals(1, customerPageTextRepo.findAll().size());

        customerPageTextService.deleteCustomerPageText(customerPageText);
        assertEquals(0, customerPageTextRepo.findAll().size());
    }

    @Test
    void getLatestCustomerPageTextByPageAndSection_shouldReturnLatest_whenSavingNewCustomerPageText() {
        for(int i = 0; i < 5; i++){
            CustomerPageText customerPageText = CustomerPageText.builder()
                    .page(TEST_STRING)
                    .section(TEST_STRING)
                    .build();
            customerPageTextService.saveCustomerPageText(customerPageText);

            assertEquals(customerPageTextService
                    .getLatestCustomerPageTextByPageAndSection(TEST_STRING, TEST_STRING), customerPageText);
        }
    }

    @Test
    void getCustomerPageTextListByPage_shouldReturnCorrectCustomerPageTexts() {
        List<CustomerPageText> customerPageTextList = new ArrayList<>();
        int i = 1;
        while(i <= 3){
            customerPageTextList.add(CustomerPageText.builder()
                    .page(TEST_STRING)
                    .section(TEST_STRING + i)
                    .build());
            i++;
        }
        customerPageTextRepo.saveAll(customerPageTextList);

        List<CustomerPageText> savedCustomerPageTextList =
                customerPageTextService.getCustomerPageTextListByPage(TEST_STRING);

        assertTrue(savedCustomerPageTextList.containsAll(customerPageTextList));
        assertEquals(3, savedCustomerPageTextList.size());
    }

    @Test
    void getCustomerPageTextListByPage_shouldNotReturnIncorrectCustomerPageTexts(){
        List<CustomerPageText> customerPageTextList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            customerPageTextList.add(CustomerPageText.builder()
                    .page(TEST_STRING)
                    .section(TEST_STRING + i)
                    .build());
        }
        customerPageTextList.add(CustomerPageText.builder()
                .page("wrong page")
                .section(TEST_STRING)
                .build());
        customerPageTextRepo.saveAll(customerPageTextList);

        List<CustomerPageText> savedCustomerPageTextList =
                customerPageTextService.getCustomerPageTextListByPage(TEST_STRING);

        assertFalse(savedCustomerPageTextList.containsAll(customerPageTextList));
        assertEquals(5, savedCustomerPageTextList.size());
    }
}