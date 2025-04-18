package com.example.demo.services;

import com.example.demo.model.TattooImage;
import com.example.demo.repos.TattooImageRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TattooImageService {
    private final TattooImageRepo tattooImageRepo;

    public TattooImageService(TattooImageRepo tattooImageRepo){
        this.tattooImageRepo = tattooImageRepo;
    }

    public List<TattooImage> getAllImages(){
        return tattooImageRepo.findAll();
    }

    public void saveListOfTattooImages(List<TattooImage> tattooImages){
        tattooImageRepo.saveAll(tattooImages);
    }

    public TattooImage findImageByUrl(String url){return tattooImageRepo.findByUrl(url);}

}
