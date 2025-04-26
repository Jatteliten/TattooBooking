package com.example.tattooPlatform.services;

import com.example.tattooPlatform.model.InstagramEmbed;
import com.example.tattooPlatform.repos.InstagramEmbedRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class InstagramEmbedService {

    private final InstagramEmbedRepo instagramEmbedRepo;

    public InstagramEmbedService(InstagramEmbedRepo instagramEmbedRepo){
        this.instagramEmbedRepo = instagramEmbedRepo;
    }

    @CacheEvict(value = "instagramEmbedHtml", allEntries = true)
    public void saveInstagramEmbed(InstagramEmbed instagramEmbed){
        instagramEmbedRepo.save(instagramEmbed);
    }

    @Cacheable(value="instagramEmbedHtml")
    public InstagramEmbed getLatestEmbed(){
        return instagramEmbedRepo.findFirstByOrderByCreatedAtDesc();
    }

    public String generateEmbedHtmlFromUrl(String url){
        return "<blockquote class=\"instagram-media\" data-instgrm-permalink=\"" + url + "\" data-instgrm-version=\"14\"></blockquote>";
    }
}
