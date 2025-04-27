document.addEventListener('DOMContentLoaded', function () {
    const rows = document.querySelectorAll('tbody tr');

    rows.forEach((row, index) => {
        const radios = row.querySelectorAll('input[type="radio"]');
        const timesContainer = row.querySelector('td:nth-child(3)');

        const originalTimesHTML = timesContainer.innerHTML;

        radios.forEach(radio => {
            radio.addEventListener('change', function () {
                if (this.value === 'dropin' && this.checked) {
                    timesContainer.innerHTML = `
                        <label>
                            Start:
                            <input type="time" name="dateList[${index}].hours" value="11:00" class="form-control" required>
                        </label>
                    `;
                } else if (this.value !== 'dropin' && this.checked) {
                    if (timesContainer.querySelector('input[type="time"]')) {
                        timesContainer.innerHTML = originalTimesHTML;
                    }
                }
            });

            if (radio.checked) {
                radio.dispatchEvent(new Event('change'));
            }
        });
    });
});