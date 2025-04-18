package com.example.demo.services;

import com.example.demo.model.InstagramEmbed;
import com.example.demo.repos.InstagramEmbedRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class InstagramEmbedService {

    private final InstagramEmbedRepo instagramEmbedRepo;

    public InstagramEmbedService(InstagramEmbedRepo instagramEmbedRepo){
        this.instagramEmbedRepo = instagramEmbedRepo;
    }

    public InstagramEmbed getLatestEmbed(){
        return instagramEmbedRepo.findAll().stream().findFirst().orElse(null);
    }

    @CacheEvict(value = "instagramEmbedHtml", allEntries = true)
    public void saveOrUpdateEmbed(String embedURL){
        InstagramEmbed existingEmbed = getLatestEmbed();
        if(existingEmbed == null){
            existingEmbed = new InstagramEmbed();
        }
        existingEmbed.setEmbeddedLink(embedURL);
        instagramEmbedRepo.save(existingEmbed);
    }

    public String generateEmbedHtmlFromUrl(String url){
        return "<blockquote class=\"instagram-media\" data-instgrm-permalink=\"" + url + "\" data-instgrm-version=\"14\"></blockquote>";
    }

    @Cacheable("instagramEmbedHtml")
    public String generateEmbedHtmlFromDatabase() {
        return "<blockquote class=\"instagram-media\" data-instgrm-permalink=\"" + getLatestEmbed().getEmbeddedLink() + "\" data-instgrm-version=\"14\"></blockquote>";
    }
}
