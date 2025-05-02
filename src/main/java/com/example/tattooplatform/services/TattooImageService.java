package com.example.tattooplatform.services;

import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.model.TattooImage;
import com.example.tattooplatform.repos.TattooImageRepo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "tattooImages")
public class TattooImageService {
    private final TattooImageRepo tattooImageRepo;

    public TattooImageService(TattooImageRepo tattooImageRepo){
        this.tattooImageRepo = tattooImageRepo;
    }

    @CacheEvict(allEntries = true)
    public void saveTattooImage(TattooImage tattooImage){
        tattooImageRepo.save(tattooImage);
    }

    @CacheEvict(allEntries = true)
    public void saveListOfTattooImages(List<TattooImage> tattooImages){
        tattooImageRepo.saveAll(tattooImages);
    }

    @CacheEvict(allEntries = true)
    public void deleteTattooImage(TattooImage tattooImage){
        tattooImageRepo.delete(tattooImage);
    }

    @CacheEvict(allEntries = true)
    public void deleteListOfTattooImages(List<TattooImage> tattooImages){
        tattooImageRepo.deleteAll(tattooImages);
    }

    @Cacheable
    public Page<TattooImage> getPageOrderedByLatestBookingDate(ImageCategory category, int page, int amount){
        return tattooImageRepo.findByCategoriesInOrderByBookingDateDesc(
                List.of(category), PageRequest.of(page, amount));
    }

    @Cacheable
    public int countTattooImagesByImageCategory(ImageCategory category){
        return tattooImageRepo.countByCategoriesIn(List.of(category));
    }

}