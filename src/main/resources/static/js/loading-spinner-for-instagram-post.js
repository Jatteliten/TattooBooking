window.addEventListener('load', function () {
    setTimeout(function () {
        document.getElementById('loading').style.display = 'none';
        document.querySelector('.instagram-outer-container').classList.add('visible');
        document.getElementById('instagram-content').classList.add('visible');
    }, 50);
});