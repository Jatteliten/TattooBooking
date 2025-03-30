document.addEventListener("DOMContentLoaded", function () {
    const checkboxes = document.querySelectorAll("input[type='checkbox']");

    checkboxes.forEach(checkbox => {
        const row = checkbox.closest("tr");
        if (!row) return;

        const dateInput = row.querySelector("input[type='hidden'][name*='date']");
        if (!dateInput) return;

        const date = new Date(dateInput.value);

        if (date.getDay() === 6) {
            checkbox.checked = false;
        }
    });
});