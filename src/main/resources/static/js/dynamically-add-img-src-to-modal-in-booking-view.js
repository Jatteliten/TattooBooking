function showImageInModal(element) {
    const imageUrl = element.getAttribute("data-image-src");
    const modalImage = document.getElementById("modalImage");
    modalImage.src = imageUrl;
}