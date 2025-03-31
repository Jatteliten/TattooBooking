let tooltip = document.getElementById('hoursDisplay');
let isMoving = false;
function showTooltip(event, element) {
    const hoursData = element.getAttribute('data-hours')
        .substring(1, element.getAttribute('data-hours').length - 1);
    if (hoursData) {
        const hoursArray = hoursData.split(',');
        let hoursText = element.getAttribute('data-date').split('-')[2] + '<hr>';
        if(hoursText.startsWith('0')){
            hoursText = hoursText.substring(1);
        }

        hoursArray.forEach(hour => {
            const [time, available] = hour.split('-');
            const availabilityClass = available === 'true' ? 'text-danger text-decoration-line-through' : 'text-success';
            hoursText += `<span class="${availabilityClass}">${time}</span><br>`;
        });

        tooltip.innerHTML = hoursText;
        tooltip.style.left = `${event.pageX + 10}px`;
        tooltip.style.top = `${event.pageY + 10}px`;
        tooltip.style.display = 'block';
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
    tooltip.style.display = 'none';
}