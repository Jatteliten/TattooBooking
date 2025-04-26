package com.example.tattooPlatform.services;

import com.example.tattooPlatform.model.TattooImage;
import com.example.tattooPlatform.repos.TattooImageRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TattooImageService {
    private final TattooImageRepo tattooImageRepo;

    public TattooImageService(TattooImageRepo tattooImageRepo){
        this.tattooImageRepo = tattooImageRepo;
    }

    public void saveTattooImage(TattooImage tattooImage){
        tattooImageRepo.save(tattooImage);
    }

    public void saveListOfTattooImages(List<TattooImage> tattooImages){
        tattooImageRepo.saveAll(tattooImages);
    }

    public void deleteTattooImage(TattooImage tattooImage){
        tattooImageRepo.delete(tattooImage);
    }

    public void deleteListOfTattooImages(List<TattooImage> tattooImages){
        tattooImageRepo.deleteAll(tattooImages);
    }

}