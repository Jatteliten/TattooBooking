window.addEventListener('load', function () {
    setTimeout(function () {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('instagram-content').classList.add('visible');
    }, 200);
});