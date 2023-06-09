const getBarberBtn = document.getElementById('get-barber');
const getBarbersNearMeBtn = document.getElementById('get-barbers-near-me');
const getBarberScheduleBtn = document.getElementById('get-barber-schedule');
const bookBarberBtn = document.getElementById('book-barber');
const cancelAppointmentBtn = document.getElementById('cancel-appointment');
const getAllMyAppointmentsBtn = document.getElementById('get-all-my-appointments')
const updateProfileBtn = document.getElementById('update-profile');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);

getBarberBtn.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`clientGetBarber.html?zip=${zip}&id=${id}`);
});

getBarbersNearMeBtn.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`clientGetBarbersNearMe.html?zip=${zip}&id=${id}`);
});

getBarberScheduleBtn.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`clientGetBarberSchedule.html?zip=${zip}&id=${id}`);
});

bookBarberBtn.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`clientBookBarber.html?zip=${zip}&id=${id}`);
});

cancelAppointmentBtn.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`clientCancelAppointment.html?zip=${zip}&id=${id}`);
});
getAllMyAppointmentsBtn.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`clientGetAllMyAppointments.html?zip=${zip}&id=${id}`);
});
updateProfileBtn.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.replace(`clientUpdateProfile.html?zip=${zip}&id=${id}`);
});

