document.addEventListener("DOMContentLoaded", function () {
    const dateRows = document.querySelectorAll("tbody tr");

    dateRows.forEach((row) => {
        const dateInput = row.querySelector("input[type='hidden'][name*='date']");
        if (!dateInput) return;

        const date = new Date(dateInput.value);
        const dayOfWeek = date.getDay();

        const radioButtons = row.querySelectorAll("input[type='radio'][name*='type']");
        if (radioButtons.length === 0) return;

        radioButtons.forEach(radio => radio.checked = false);

        if (dayOfWeek === 6) {
        } else if (dayOfWeek === 2) {
            radioButtons.forEach(radio => {
                if (radio.value === "touchup") {
                    radio.checked = true;
                }
            });
        } else {
            radioButtons.forEach(radio => {
                if (radio.value === "standard") {
                    radio.checked = true;
                }
            });
        }
    });
});