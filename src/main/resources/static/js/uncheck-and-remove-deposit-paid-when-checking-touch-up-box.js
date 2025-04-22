document.addEventListener('DOMContentLoaded', function () {
    const touchUp = document.getElementById('touchUpCheckbox');
    const depositPaid = document.getElementById('depositPaidCheckbox');
    const depositWrapper = document.getElementById('depositPaidWrapper');
    const touchUpWrapper = document.getElementById('touchUpWrapper');

    touchUp.addEventListener('change', function () {
        if (touchUp.checked) {
            depositPaid.checked = false;
            depositWrapper.style.display = 'none';
        } else {
            depositWrapper.style.display = 'inline-block';
        }
    });

    depositPaid.addEventListener('change', function () {
        if (depositPaid.checked) {
            touchUp.checked = false;
            touchUpWrapper.style.display = 'none';
        } else {
            touchUpWrapper.style.display = 'inline-block';
        }
    });
});