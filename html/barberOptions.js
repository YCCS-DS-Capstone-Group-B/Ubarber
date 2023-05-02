const optionButtons = document.getElementById('option-buttons');
const addScheduleButton = document.getElementById('add-schedule');
const cancelAppointmentButton = document.getElementById('cancel-appointment');
const updateProfileButton = document.getElementById('update-profile');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);

addScheduleButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberAddSchedule.html?zip=${zip}&id=${id}`);
});
cancelAppointmentButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberCancelAppointment.html?zip=${zip}&id=${id}`);
});
updateProfileButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberUpdateProfile.html?zip=${zip}&id=${id}`);
});
