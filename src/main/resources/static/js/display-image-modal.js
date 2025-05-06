let imageModal = document.getElementById('imageModal');
let modalImage = imageModal.querySelector('#modalImage');
let modalCaption = imageModal.querySelector('#modalCaption');
let nextBtn = document.getElementById('nextImage');
let prevBtn = document.getElementById('prevImage');

let images = [];
let captions = [];
let currentIndex = 0;

imageModal.addEventListener('show.bs.modal', function (event) {
    let button = event.relatedTarget;

    images = Array.from(document.querySelectorAll('[data-bs-image]')).map(btn => btn.getAttribute('data-bs-image'));
    captions = Array.from(document.querySelectorAll('[data-bs-image]')).map(btn => btn.getAttribute('data-bs-caption'));

    let imageUrl = button.getAttribute('data-bs-image');

    currentIndex = images.indexOf(imageUrl);

    showImage();
});

function showImage() {
    if (images.length <= 1) {
        nextBtn.style.display = 'none';
        prevBtn.style.display = 'none';
    } else {
        nextBtn.style.display = 'inline-block';
        prevBtn.style.display = 'inline-block';
    }

    if (images.length > 0) {
        modalImage.src = images[currentIndex];
        modalCaption.textContent = captions[currentIndex];
    }
}

nextBtn.addEventListener('click', function () {
    if (images.length > 0) {
        currentIndex = (currentIndex + 1) % images.length;
        showImage();
    }
});

prevBtn.addEventListener('click', function () {
    if (images.length > 0) {
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        showImage();
    }
});
