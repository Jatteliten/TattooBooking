document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("bookingForm");
    const contactError = document.getElementById("contactError");

    form.addEventListener("submit", function (e) {
        const email = document.getElementById("mail").value.trim();
        const instagram = document.getElementById("instagram").value.trim();
        const phone = document.getElementById("phone").value.trim();

        if (!email && !instagram && !phone) {
            e.preventDefault();

            contactError.textContent = "Fyll i minst ett av fälten: Email, Instagram eller Telefon.";
            contactError.style.display = "block";

            contactError.scrollIntoView({ behavior: "smooth", block: "center" });

            return;
        }

        contactError.textContent = "";
        contactError.style.display = "none";
    });
});
