package com.example.demo.services;

import com.example.demo.model.TattooImage;
import com.example.demo.model.dtos.tattooImages.TattooImageUrlDto;
import com.example.demo.repos.TattooImageRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TattooImageService {
    TattooImageRepo tattooImageRepo;

    public TattooImageService(TattooImageRepo tattooImageRepo){
        this.tattooImageRepo = tattooImageRepo;
    }
    public List<TattooImage> getAllImages(){
        return tattooImageRepo.findAll();
    }
    public List<TattooImage> getAllFrontPageImages(){
        return tattooImageRepo.findByFrontPageTrue();
    }
    public List<TattooImage> getAllNonFrontPageImages(){
        return tattooImageRepo.findByFrontPageFalse();
    }

    public TattooImageUrlDto convertTattooImageToTattooImageUrlDto(TattooImage tattooImage){
        return TattooImageUrlDto.builder().url(tattooImage.getUrl()).build();
    }

    public List<TattooImageUrlDto> getAllFrontPageImagesUrls(){
        return getAllFrontPageImages()
                .stream()
                .map(this::convertTattooImageToTattooImageUrlDto)
                .collect(Collectors.toList());
    }
}
