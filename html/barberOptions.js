const optionButtons = document.getElementById('option-buttons');
const cancelAppointmentButton = document.getElementById('cancel-appointment');
const updateProfileButton = document.getElementById('update-profile');
const addAppointmentSlotButton = document.getElementById('add-appointment-slot');
const deleteAppointmentSlotButton = document.getElementById('delete-appointment-slot');
const updateAppointmentSlotButton = document.getElementById('update-appointment-slot');
const barberGetAllMyAppointmentsButton = document.getElementById('get-all-my-appointments');
const barberGetAllMyAppointmentSlotsButton = document.getElementById('get-all-my-appointment-slots');

const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);

cancelAppointmentButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberCancelAppointment.html?zip=${zip}&id=${id}`);
});
updateProfileButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberUpdateProfile.html?zip=${zip}&id=${id}`);
});
addAppointmentSlotButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberAddAppointmentSlot.html?zip=${zip}&id=${id}`);
});
deleteAppointmentSlotButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberDeleteAppointmentSlot.html?zip=${zip}&id=${id}`);
});
updateAppointmentSlotButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberUpdateAppointmentSlot.html?zip=${zip}&id=${id}`);
});
barberGetAllMyAppointmentsButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberGetAllMyAppointments.html?zip=${zip}&id=${id}`);
});
barberGetAllMyAppointmentSlotsButton.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`barberGetAllMyAppointmentSlots.html?zip=${zip}&id=${id}`);
});
