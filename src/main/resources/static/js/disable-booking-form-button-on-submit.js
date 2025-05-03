document.getElementById('bookingForm').addEventListener('submit', function() {
    const submitButton = this.querySelector('button[type="submit"]');
    submitButton.disabled = true;
    submitButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Skickar...';
});