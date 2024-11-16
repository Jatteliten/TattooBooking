function addTimeInput() {
    const timeInputs = document.getElementById('timeInputs');
    const timeGroup = document.createElement('div');
    timeGroup.className = 'time-group';
    timeGroup.innerHTML = `
    <input type="time" name="time" required>
    <button type="button" class="btn btn-danger remove-time" onclick="removeTimeInput(this)">Remove</button>
  `;
    timeInputs.appendChild(timeGroup);
}

function removeTimeInput(button) {
    const timeGroup = button.parentElement;
    timeGroup.remove();
}
