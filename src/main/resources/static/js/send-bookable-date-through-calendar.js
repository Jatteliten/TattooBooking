document.addEventListener("DOMContentLoaded", function () {
    const table = document.querySelector("table[data-base-link]");
    if (!table) return;

    const baseLink = table.getAttribute("data-base-link");

    document.querySelectorAll(".customer-calendar-column").forEach(function (cell) {
        const selectedDate = cell.getAttribute("data-date");
        if (selectedDate && !cell.classList.contains("not-current-month")) {
            const link = document.createElement("a");
            link.href = `${baseLink}?date=${selectedDate}`;
            link.textContent = cell.textContent;
            link.style.textDecoration = "none";
            link.style.color = "inherit";
            link.style.display = "block";
            link.style.width = "100%";
            link.style.height = "100%";

            cell.textContent = "";
            cell.appendChild(link);
        }
    });
});
