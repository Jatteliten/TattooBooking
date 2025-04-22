document.addEventListener('DOMContentLoaded', function () {
    const touchUp = document.getElementById('touchUpCheckbox');
    const depositPaid = document.getElementById('depositPaidCheckbox');
    const depositWrapper = document.getElementById('depositPaidWrapper');

    touchUp.addEventListener('change', function () {
        if (touchUp.checked) {
            depositPaid.checked = false;
            depositWrapper.style.display = 'none';
        } else {
            depositWrapper.style.display = 'inline-block'; // or 'block' depending on layout
        }
    });
});