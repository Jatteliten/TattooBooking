let imageModal = document.getElementById('imageModal');
imageModal.addEventListener('show.bs.modal', function (event) {
    let button = event.relatedTarget;
    let imageUrl = button.getAttribute('data-bs-image');
    let captionText = button.getAttribute('data-bs-caption');

    let modalImage = imageModal.querySelector('#modalImage');
    let modalCaption = imageModal.querySelector('#modalCaption');

    modalImage.src = imageUrl;
    modalCaption.textContent = captionText;
});