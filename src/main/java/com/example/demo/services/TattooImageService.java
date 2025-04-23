package com.example.demo.services;

import com.example.demo.model.TattooImage;
import com.example.demo.repos.TattooImageRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TattooImageService {
    private final TattooImageRepo tattooImageRepo;

    public TattooImageService(TattooImageRepo tattooImageRepo){
        this.tattooImageRepo = tattooImageRepo;
    }

    @CacheEvict(value = {"tattooImagesByUrl", "allTattooImages"}, allEntries = true)
    public void saveTattooImage(TattooImage tattooImage){
        tattooImageRepo.save(tattooImage);
    }

    @CacheEvict(value = {"tattooImagesByUrl", "allTattooImages"}, allEntries = true)
    public void deleteTattooImage(TattooImage tattooImage){
        tattooImageRepo.delete(tattooImage);
    }

    @CacheEvict(value = {"tattooImagesByUrl", "allTattooImages"}, allEntries = true)
    public void deleteListOfTattooImages(List<TattooImage> tattooImages){
        tattooImageRepo.deleteAll(tattooImages);
    }

    @CacheEvict(value = {"tattooImagesByUrl", "allTattooImages"}, allEntries = true)
    public void saveListOfTattooImages(List<TattooImage> tattooImages){
        tattooImageRepo.saveAll(tattooImages);
    }

    @Cacheable(value = "allTattooImages")
    public List<TattooImage> getAllImages(){
        return tattooImageRepo.findAll();
    }

    @Cacheable(value = "tattooImagesByUrl", key = "#url")
    public TattooImage findImageByUrl(String url){
        return tattooImageRepo.findByUrl(url);
    }
}