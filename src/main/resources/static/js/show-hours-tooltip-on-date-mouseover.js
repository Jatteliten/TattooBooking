document.addEventListener('DOMContentLoaded', () => {
    const tooltip = document.getElementById('hoursDisplay');
    let isMoving = false;

    function showTooltip(event, element) {
        const hoursData = element.getAttribute('data-hours')
            ?.slice(1, -1);
        if (hoursData) {
            const hoursArray = hoursData.split(',');
            let hoursText = element.getAttribute('data-date').split('-')[2] + '<hr>';
            if (hoursText.startsWith('0')) {
                hoursText = hoursText.substring(1);
            }

            hoursArray.forEach(hour => {
                const [time, available] = hour.split('-');
                const availabilityClass = available === 'true' ? 'text-danger text-decoration-line-through' : 'text-success';
                hoursText += `<span class="${availabilityClass}">${time}</span>`;
            });

            tooltip.innerHTML = hoursText;
            tooltip.style.left = `${event.pageX + 10}px`;
            tooltip.style.top = `${event.pageY + 10}px`;
            tooltip.classList.remove('tooltip-hidden');
            tooltip.classList.add('tooltip-visible');
        }
    }

    function moveTooltip(event) {
        if (!isMoving) {
            isMoving = true;
            requestAnimationFrame(() => {
                tooltip.style.left = `${event.pageX + 10}px`;
                tooltip.style.top = `${event.pageY + 10}px`;
                isMoving = false;
            });
        }
    }


    function hideTooltip() {
        tooltip.classList.remove('tooltip-visible');
        tooltip.classList.add('tooltip-hidden');
    }

    const table = document.querySelector('table');
    if (table) {
        table.addEventListener('mouseover', (event) => {
            const target = event.target.closest('.calendar-column');
            if (target) {
                showTooltip(event, target);
            }
        });

        table.addEventListener('click', (event) => {
            const target = event.target.closest('.calendar-column');
            if (target) {
                showTooltip(event, target);
            }
        });

        table.addEventListener('mousemove', (event) => {
            moveTooltip(event);
        });

        table.addEventListener('mouseout', (event) => {
            const target = event.target.closest('.calendar-column');
            if (target) {
                hideTooltip();
            }
        });
    }
});
