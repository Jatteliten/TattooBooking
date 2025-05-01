package com.example.tattooPlatform.services;

import com.example.tattooPlatform.model.InstagramEmbed;
import com.example.tattooPlatform.repos.InstagramEmbedRepo;
import org.springframework.stereotype.Service;

@Service
public class InstagramEmbedService {

    private final InstagramEmbedRepo instagramEmbedRepo;

    public InstagramEmbedService(InstagramEmbedRepo instagramEmbedRepo){
        this.instagramEmbedRepo = instagramEmbedRepo;
    }

    public void saveInstagramEmbed(InstagramEmbed instagramEmbed){
        instagramEmbedRepo.save(instagramEmbed);
    }

    public InstagramEmbed getLatestEmbed(){
        return instagramEmbedRepo.findFirstByOrderByCreatedAtDesc();
    }

    public String generateEmbedHtmlFromUrl(String url){
        return "<blockquote class=\"instagram-media\" data-instgrm-permalink=\"" + url + "\" data-instgrm-version=\"14\"></blockquote>";
    }
}
