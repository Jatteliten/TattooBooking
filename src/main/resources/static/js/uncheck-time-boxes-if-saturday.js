document.addEventListener("DOMContentLoaded", function() {
    const checkboxes = document.querySelectorAll("input[type='checkbox']");

    checkboxes.forEach(checkbox => {
        const datePart = checkbox.value.split("=")[0];
        const date = new Date(datePart);

        if (date.getDay() === 6) {
            checkbox.checked = false;
        }
    });
});