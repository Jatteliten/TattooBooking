package com.example.tattooPlatform.services;

import com.example.tattooPlatform.model.InstagramEmbed;
import com.example.tattooPlatform.repos.InstagramEmbedRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class InstagramEmbedServiceTest {
    @Autowired
    InstagramEmbedRepo instagramEmbedRepo;
    @Autowired
    InstagramEmbedService instagramEmbedService;

    @AfterEach
    void deleteAll(){
        instagramEmbedRepo.deleteAll();
    }

    @Test
    void saveInstagramEmbed_shouldSaveInstagramEmbed() {
        instagramEmbedService.saveInstagramEmbed(InstagramEmbed.builder().build());

        assertEquals(1, instagramEmbedRepo.findAll().size());
    }

    @Test
    void getLatestEmbed_shouldGetLatestEmbed() {
        for(int i = 0; i < 5; i++) {
            InstagramEmbed instagramEmbed = InstagramEmbed.builder()
                    .createdAt(LocalDateTime.now())
                    .build();
            instagramEmbedRepo.save(instagramEmbed);

            assertEquals(instagramEmbed.getId(), instagramEmbedService.getLatestEmbed().getId());
        }
    }

    @Test
    void generateEmbedHtmlFromUrl_shouldCreateCorrectUrl() {
        String expectedUrl = "<blockquote class=\"instagram-media\" data-instgrm-permalink=\"test\" data-instgrm-version=\"14\"></blockquote>";

        assertEquals(expectedUrl, instagramEmbedService.generateEmbedHtmlFromUrl("test"));
    }
}