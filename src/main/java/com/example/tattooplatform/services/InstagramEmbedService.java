package com.example.tattooplatform.services;

import com.example.tattooplatform.model.InstagramEmbed;
import com.example.tattooplatform.repos.InstagramEmbedRepo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "instagramEmbeds")
public class InstagramEmbedService {

    private final InstagramEmbedRepo instagramEmbedRepo;

    public InstagramEmbedService(InstagramEmbedRepo instagramEmbedRepo){
        this.instagramEmbedRepo = instagramEmbedRepo;
    }

    @CacheEvict(allEntries = true)
    public void saveInstagramEmbed(InstagramEmbed instagramEmbed){
        instagramEmbedRepo.save(instagramEmbed);
    }

    @Cacheable
    public InstagramEmbed getLatestEmbed(){
        return instagramEmbedRepo.findFirstByOrderByCreatedAtDesc();
    }

    public String generateEmbedHtmlFromUrl(String url){
        return "<blockquote class=\"instagram-media\" data-instgrm-permalink=\"" + url + "\" data-instgrm-version=\"14\"></blockquote>";
    }
}
