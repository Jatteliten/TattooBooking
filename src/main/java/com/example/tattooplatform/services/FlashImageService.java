package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.flashimage.FlashImageUrlDto;
import com.example.tattooplatform.model.FlashImage;
import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.repos.FlashImageRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FlashImageService {
    private final FlashImageRepo flashImageRepo;

    public FlashImageService(FlashImageRepo flashImageRepo){
        this.flashImageRepo = flashImageRepo;
    }

    public void saveFlashImage(FlashImage flashImage){
        flashImageRepo.save(flashImage);
    }

    public void saveListOfFlashImages(List<FlashImage> flashImages){
        flashImageRepo.saveAll(flashImages);
    }

    public void deleteFlashImage(FlashImage flashImage){
        flashImageRepo.delete(flashImage);
    }

    public FlashImage getFlashImageById(UUID id){
        return flashImageRepo.findById(id).orElse(null);
    }

    public Page<FlashImage> getFlashImagesByCategoryPaginated(ImageCategory imageCategory, Pageable pageable){
        return flashImageRepo.findByCategoriesContaining(imageCategory, pageable);
    }

    public Page<FlashImageUrlDto> getFlashImageUrlDTOssByCategoryPaginated(ImageCategory imageCategory, Pageable pageable) {
        return flashImageRepo.findByCategoriesContaining(imageCategory, pageable)
                .map(this::convertFlashImageToFlashImageUrlDto);
    }

    public FlashImageUrlDto convertFlashImageToFlashImageUrlDto(FlashImage flashImage){
        return FlashImageUrlDto.builder()
                .url(flashImage.getUrl())
                .build();
    }

}
