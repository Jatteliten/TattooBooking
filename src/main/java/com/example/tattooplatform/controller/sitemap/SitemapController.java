package com.example.tattooplatform.controller.sitemap;

import com.example.tattooplatform.services.ImageCategoryService;
import com.example.tattooplatform.services.ProductCategoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class SitemapController {

    private final ProductCategoryService productCategoryService;
    private final ImageCategoryService imageCategoryService;

    public SitemapController(ProductCategoryService productCategoryService, ImageCategoryService imageCategoryService) {
        this.productCategoryService = productCategoryService;
        this.imageCategoryService = imageCategoryService;
    }

    private static final List<String> URLS = List.of(
            "https://levibuet.se/",
            "https://levibuet.se/frequently-asked-questions",
            "https://levibuet.se/before-your-visit",
            "https://levibuet.se/care-advice",
            "https://levibuet.se/booking-form",
            "https://levibuet.se/flash",
            "https://levibuet.se/portfolio",
            "https://levibuet.se/about-me",
            "https://levibuet.se/privacy-notice",
            "https://levibuet.se/products"
    );

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        String now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        StringBuilder sitemapBuilder = new StringBuilder();
        sitemapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sitemapBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        for (String url : URLS) {
            sitemapBuilder.append("  <url>\n");
            sitemapBuilder.append("    <loc>").append(xmlEscape(url)).append("</loc>\n");
            sitemapBuilder.append("    <lastmod>").append(now).append("</lastmod>\n");
            sitemapBuilder.append("    <priority>").append(url.equals("https://levibuet.se/") ? "1.00" : "0.80").append("</priority>\n");
            sitemapBuilder.append("  </url>\n");
        }

        productCategoryService.filterOutProductCategoriesWithoutProducts(
                productCategoryService.getAllProductCategories()).forEach(category -> {
            sitemapBuilder.append("  <url>\n");
            sitemapBuilder.append("    <loc>").append(safeUrl("https://levibuet.se/products/", category.getName())).append("</loc>\n");
            sitemapBuilder.append("    <lastmod>").append(now).append("</lastmod>\n");
            sitemapBuilder.append("    <priority>0.80</priority>\n");
            sitemapBuilder.append("  </url>\n");
        });

        imageCategoryService.filterImageCategoriesWithoutFlashImages(
                imageCategoryService.getAllImageCategories()).forEach(category -> {
            sitemapBuilder.append("  <url>\n");
            sitemapBuilder.append("    <loc>").append(safeUrl("https://levibuet.se/flash/", category.getCategory())).append("</loc>\n");
            sitemapBuilder.append("    <lastmod>").append(now).append("</lastmod>\n");
            sitemapBuilder.append("    <priority>0.80</priority>\n");
            sitemapBuilder.append("  </url>\n");
        });

        sitemapBuilder.append("</urlset>");

        return sitemapBuilder.toString();
    }

    private String encodePathSegment(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    private String xmlEscape(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String safeUrl(String baseUrl, String pathSegment) {
        return xmlEscape(baseUrl + encodePathSegment(pathSegment));
    }
}
