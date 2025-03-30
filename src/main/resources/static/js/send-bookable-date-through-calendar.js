document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".customer-calendar-column").forEach(function (cell) {
        let selectedDate = cell.getAttribute("data-date");
        if (selectedDate && !cell.classList.contains("not-current-month")) {
            let link = document.createElement("a");
            link.href = "/booking/book-tattoo-at-date?date=" + selectedDate;
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
