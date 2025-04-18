document.getElementById('createCustomerForm').addEventListener('submit', function (e) {
    const instagram = document.getElementById('instagram').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const email = document.getElementById('email').value.trim();

    if (!instagram && !phone && !email) {
        e.preventDefault();
        alert("Please provide at least one contact method: Instagram, Phone, or Email.");
    }
});