package com.example.tattooplatform.services;

import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.model.TattooImage;
import com.example.tattooplatform.repos.TattooImageRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Page<TattooImage> getPageOrderedByLatestBookingDate(ImageCategory category, int page, int amount){
        return tattooImageRepo.findByCategoriesInOrderByBookingDateDesc(
                List.of(category), PageRequest.of(page, amount));
    }

    public int countTattooImagesByImageCategory(ImageCategory category){
        return tattooImageRepo.countByCategoriesIn(List.of(category));
    }

}