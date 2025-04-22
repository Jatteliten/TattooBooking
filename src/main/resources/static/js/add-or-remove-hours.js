function addTimeInput() {
    const timeInputs = document.getElementById('timeInputs');
    const timeGroup = document.createElement('div');
    timeGroup.className = 'time-group';
    timeGroup.innerHTML = `
        <input type="time" name="time" required>
        <button type="button" class="btn btn-danger btn-sm remove-time" onclick="removeTimeInput(this)">Remove</button>
    `;
    timeInputs.appendChild(timeGroup);

    updateRemoveButtons();
}

function removeTimeInput(button) {
    const timeGroup = button.parentElement;
    timeGroup.remove();

    updateRemoveButtons();
}

function updateRemoveButtons() {
    const groups = document.querySelectorAll('#timeInputs .time-group');
    const removeButtons = document.querySelectorAll('#timeInputs .remove-time');

    if (groups.length === 1) {
        removeButtons.forEach(btn => btn.style.display = 'none');
    } else {
        removeButtons.forEach(btn => btn.style.display = 'inline-block');
    }
}