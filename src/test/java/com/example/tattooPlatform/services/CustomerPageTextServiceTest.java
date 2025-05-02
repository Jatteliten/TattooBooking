package com.example.tattooPlatform.services;

import com.example.tattooPlatform.dto.customerpagetext.CustomerPageTextDto;
import com.example.tattooPlatform.dto.customerpagetext.CustomerPageTextPrioritizedDto;
import com.example.tattooPlatform.model.CustomerPageText;
import com.example.tattooPlatform.repos.CustomerPageTextRepo;
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
    private static final String TEST_PAGE = "test page";
    private static final String TEST_SECTION = "test section";

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
                    .page(TEST_PAGE)
                    .section(TEST_SECTION)
                    .build();
            customerPageTextService.saveCustomerPageText(customerPageText);

            assertEquals(customerPageTextService
                    .getLatestCustomerPageTextByPageAndSection(TEST_PAGE, TEST_SECTION), customerPageText);
        }
    }

    @Test
    void getCustomerPageTextListByPage_shouldReturnCorrectCustomerPageTexts() {
        List<CustomerPageText> customerPageTextList = new ArrayList<>();
        int i = 1;
        while(i <= 3){
            customerPageTextList.add(CustomerPageText.builder()
                    .page(TEST_PAGE)
                    .section(TEST_SECTION + i)
                    .build());
            i++;
        }
        customerPageTextRepo.saveAll(customerPageTextList);

        List<CustomerPageText> savedCustomerPageTextList =
                customerPageTextService.getCustomerPageTextListByPage(TEST_PAGE);

        assertTrue(savedCustomerPageTextList.containsAll(customerPageTextList));
        assertEquals(3, savedCustomerPageTextList.size());
    }

    @Test
    void getCustomerPageTextListByPage_shouldNotReturnIncorrectCustomerPageTexts(){
        List<CustomerPageText> customerPageTextList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            customerPageTextList.add(CustomerPageText.builder()
                    .page(TEST_PAGE)
                    .build());
        }
        customerPageTextList.add(CustomerPageText.builder()
                .page("wrong page")
                .build());
        customerPageTextRepo.saveAll(customerPageTextList);

        List<CustomerPageText> savedCustomerPageTextList =
                customerPageTextService.getCustomerPageTextListByPage(TEST_PAGE);

        assertFalse(savedCustomerPageTextList.containsAll(customerPageTextList));
        assertEquals(5, savedCustomerPageTextList.size());
    }

    @Test
    void getCustomerPageTextListPyPageSortedByPriority_shouldSortCorrectly(){
        CustomerPageText customerPageTextOne = CustomerPageText.builder()
                .page(TEST_PAGE)
                .priority(2)
                .build();
        CustomerPageText customerPageTextTwo = CustomerPageText.builder()
                .page(TEST_PAGE)
                .priority(1)
                .build();
        customerPageTextRepo.saveAll(List.of(customerPageTextOne, customerPageTextTwo));

        List<CustomerPageText> savedCustomerPageTextList =
                customerPageTextService.getCustomerPageTextListByPageSortedByAscendingPriority(TEST_PAGE);

        assertEquals(2, savedCustomerPageTextList.size());
        assertEquals(savedCustomerPageTextList.get(0), customerPageTextTwo);
        assertEquals(savedCustomerPageTextList.get(1), customerPageTextOne);
    }

    @Test
    void getCustomerPageTextListPyPageSortedByPriority_shouldNotReturnIncorrectPageTexts(){
        CustomerPageText customerPageTextOne = CustomerPageText.builder()
                .page(TEST_PAGE)
                .priority(1)
                .build();
        CustomerPageText customerPageTextTwo = CustomerPageText.builder()
                .page("wrong page")
                .priority(2)
                .build();
        customerPageTextRepo.saveAll(List.of(customerPageTextOne, customerPageTextTwo));

        List<CustomerPageText> savedCustomerPageTextList =
                customerPageTextService.getCustomerPageTextListByPageSortedByAscendingPriority(TEST_PAGE);

        assertEquals(1, savedCustomerPageTextList.size());
        assertTrue(savedCustomerPageTextList.contains(customerPageTextOne));
        assertFalse(savedCustomerPageTextList.contains(customerPageTextTwo));
    }

    @Test
    void getCustomerPageTextByPageAndPriority_shouldGetCorrectCustomerPageText(){
        CustomerPageText customerPageTextOne = CustomerPageText.builder()
                .page(TEST_PAGE)
                .text("one")
                .priority(1)
                .build();
        CustomerPageText customerPageTextTwo = CustomerPageText.builder()
                .page(TEST_PAGE)
                .text("two")
                .priority(2)
                .build();
        customerPageTextRepo.saveAll(List.of(customerPageTextOne, customerPageTextTwo));

        assertEquals("one",
                customerPageTextService.getCustomerPageTextByPageAndPriority(TEST_PAGE, 1).getText());
    }

    @Test
    void countCustomerPageTextsByPage_shouldCountCorrectly(){
        int i = 0;
        List<CustomerPageText> customerPageTexts = new ArrayList<>();
        while(i < 5){
            customerPageTexts.add(CustomerPageText.builder()
                    .page(TEST_PAGE)
                    .build());
            i++;
        }
        customerPageTextRepo.saveAll(customerPageTexts);

        assertEquals(i, customerPageTextService.countCustomerPageTextsByPage(TEST_PAGE));
    }

    @Test
    void convertCustomerPageTextToCustomerPageTextDto_shouldConvertCorrectly(){
        CustomerPageText customerPageText = CustomerPageText.builder()
                .text("test")
                .section(TEST_PAGE)
                .build();

        CustomerPageTextDto customerPageTextDto =
                customerPageTextService.convertCustomerPageTextToCustomerPageTextDto(customerPageText);

        assertEquals(customerPageText.getText(), customerPageTextDto.getText());
        assertEquals(customerPageText.getSection(), customerPageTextDto.getSection());
    }

    @Test
    void convertCustomerPageTextToCustomerPageTextPrioritizedDto_shouldConvertCorrectly(){
        CustomerPageText customerPageText = CustomerPageText.builder()
                .text("test")
                .section(TEST_PAGE)
                .priority(1)
                .build();

        CustomerPageTextPrioritizedDto customerPageTextDto =
                customerPageTextService.convertCustomerPageTextToCustomerPageTextPrioritizedDto(customerPageText);

        assertEquals(customerPageText.getText(), customerPageTextDto.getText());
        assertEquals(customerPageText.getSection(), customerPageTextDto.getSection());
        assertEquals(customerPageText.getPriority(), customerPageTextDto.getPriority());
    }

    @Test
    void convertCustomerPageTextListToCustomerPageTextPrioritizedDtoList_shouldConvertCorrectly(){
        CustomerPageText customerPageTextOne = CustomerPageText.builder()
                .text("test")
                .section(TEST_PAGE)
                .priority(1)
                .build();
        CustomerPageText customerPageTextTwo = CustomerPageText.builder()
                .text("test")
                .section(TEST_PAGE)
                .priority(1)
                .build();

        List<CustomerPageTextPrioritizedDto> customerPageTextPrioritizedDTOs =
                customerPageTextService.convertCustomerPageTextListToCustomerPageTextPrioritizedDtoList(
                        List.of(customerPageTextOne, customerPageTextTwo));

        assertEquals(customerPageTextOne.getText(), customerPageTextPrioritizedDTOs.get(0).getText());
        assertEquals(customerPageTextOne.getSection(), customerPageTextPrioritizedDTOs.get(0).getSection());
        assertEquals(customerPageTextOne.getPriority(), customerPageTextPrioritizedDTOs.get(0).getPriority());
        assertEquals(customerPageTextTwo.getText(), customerPageTextPrioritizedDTOs.get(1).getText());
        assertEquals(customerPageTextTwo.getSection(), customerPageTextPrioritizedDTOs.get(1).getSection());
        assertEquals(customerPageTextTwo.getPriority(), customerPageTextPrioritizedDTOs.get(1).getPriority());
    }

    @Test
    void reassignPrioritiesInList_shouldAssignPriorities_inAscendingOrder(){
        List <CustomerPageText> customerPageTexts = List.of(
                CustomerPageText.builder()
                        .priority(2)
                        .build(),
                CustomerPageText.builder()
                        .priority(1)
                        .build(),
                CustomerPageText.builder()
                        .priority(3)
                        .build());

        List<CustomerPageText> reorderedList = customerPageTextService.reassignPrioritiesInList(customerPageTexts);

        assertEquals(1, reorderedList.get(0).getPriority());
        assertEquals(2, reorderedList.get(1).getPriority());
        assertEquals(3, reorderedList.get(2).getPriority());
    }

    @Test
    void switchPriorities_shouldDecrement_whenSetToDecrement(){
        CustomerPageText customerPageTextToIncrement = CustomerPageText.builder()
                .page(TEST_PAGE)
                .section("decremented")
                .priority(2)
                .build();
        CustomerPageText customerPageTextToReplace = CustomerPageText.builder()
                .page(TEST_PAGE)
                .section("incremented")
                .priority(1)
                .build();

        List<CustomerPageText> switchedPrioritiesList = customerPageTextService.switchPriorities(
                true, customerPageTextToIncrement, customerPageTextToReplace);

        assertTrue(switchedPrioritiesList.get(0).getSection().equals(customerPageTextToIncrement.getSection()) &&
                switchedPrioritiesList.get(0).getPriority().equals(1));
        assertTrue(switchedPrioritiesList.get(1).getSection().equals(customerPageTextToReplace.getSection()) &&
                switchedPrioritiesList.get(1).getPriority().equals(2));
    }

    @Test
    void switchPriorities_shouldIncrement_whenSetToIncrement(){
        CustomerPageText customerPageTextToIncrement = CustomerPageText.builder()
                .page(TEST_PAGE)
                .section("incremented")
                .priority(1)
                .build();
        CustomerPageText customerPageTextToReplace = CustomerPageText.builder()
                .page(TEST_PAGE)
                .section("decremented")
                .priority(2)
                .build();

        List<CustomerPageText> switchedPrioritiesList = customerPageTextService.switchPriorities(
                false, customerPageTextToIncrement, customerPageTextToReplace);

        assertTrue(switchedPrioritiesList.get(0).getSection().equals(customerPageTextToIncrement.getSection()) &&
                switchedPrioritiesList.get(0).getPriority().equals(2));
        assertTrue(switchedPrioritiesList.get(1).getSection().equals(customerPageTextToReplace.getSection()) &&
                switchedPrioritiesList.get(1).getPriority().equals(1));
    }

    @Test
    void switchPriorities_shouldReturnNull_ifCustomerPageTextsAreNotNextToEachOtherInPriority(){
        CustomerPageText customerPageTextToIncrement = CustomerPageText.builder()
                .page(TEST_PAGE)
                .priority(1)
                .build();
        CustomerPageText customerPageTextToReplace = CustomerPageText.builder()
                .page(TEST_PAGE)
                .priority(5)
                .build();
        assertNull(customerPageTextService.switchPriorities(true, customerPageTextToIncrement, customerPageTextToReplace));
    }

    @Test
    void switchPriorities_shouldReturnNull_ifCustomerPageTextsAreNotFromTheSamePage(){
        CustomerPageText customerPageTextToIncrement = CustomerPageText.builder()
                .page(TEST_PAGE)
                .priority(1)
                .build();
        CustomerPageText customerPageTextToReplace = CustomerPageText.builder()
                .page(TEST_PAGE + 2)
                .priority(5)
                .build();
        assertNull(customerPageTextService.switchPriorities(true, customerPageTextToIncrement, customerPageTextToReplace));
    }
}