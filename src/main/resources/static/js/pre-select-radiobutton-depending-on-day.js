document.addEventListener("DOMContentLoaded", function() {
    const dateElements = document.querySelectorAll("tr");

    dateElements.forEach((row) => {
        const dateElement = row.querySelector("span");
        if (dateElement) {
            const dateStr = dateElement.textContent.trim();
            const date = new Date(dateStr);
            const dayOfWeek = date.getDay();

            const standardRadio = row.querySelector(`#standard-${dateStr}`);
            const dropinRadio = row.querySelector(`#dropin-${dateStr}`);
            const touchupRadio = row.querySelector(`#touchup-${dateStr}`);

            if (dayOfWeek === 6) {
                standardRadio.checked = false;
                dropinRadio.checked = false;
                touchupRadio.checked = false;
            } else if (dayOfWeek === 2) {
                touchupRadio.checked = true;
            } else {
                standardRadio.checked = true;
            }
        }
    });
});
