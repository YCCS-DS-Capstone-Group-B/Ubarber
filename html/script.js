const profileForm = document.getElementById('profile-form');
const optionButtons = document.getElementById('option-buttons');
const addScheduleButton = document.getElementById('add-schedule');
const createProfileHeader = document.getElementById('formHeader');
const bookedAppointmentsButton = document.getElementById('booked-appointments');
const nearbyBarbersButton = document.getElementById('nearby-barbers');
const allBarbersButton = document.getElementById('all-barbers');
const bookAppointmentButton = document.getElementById('book-appointment');
const cancelAppointmentButton = document.getElementById('cancel-appointment');
const viewBookedAppointmentsButton = document.getElementById('booked-appointments');
const findAppointmentButton = document.getElementById('find-appointment');
const viewBookedAppointmentsPage = document.getElementById('view-booked-appointments-page');
const addSchedulePage = document.getElementById('add-schedule-page');
const nearbyBarbersPage = document.getElementById('nearby-barbers-page');
const allBarbersPage = document.getElementById('all-barbers-page');
const bookAppointmentPage = document.getElementById('book-appointment-page');
const cancelAppointmentPage = document.getElementById('cancel-appointment-page');
const findAppointmentPage = document.getElementById('find-appointment-page');
const logo = document.getElementById('logo-pic');

profileForm.addEventListener('submit', (event) => {
    event.preventDefault();
    profileForm.style.display = 'none'; // hide the form
    createProfileHeader.style.display = 'none';
    logo.style.display = 'none'; // hide the logo
    optionButtons.style.display = 'block'; // show the option buttons
    registerType = "";
    if (document.getElementById('barber').checked) {
        addScheduleButton.style.display = 'block';
        nearbyBarbersButton.style.display = 'none';
        allBarbersButton.style.display = 'none';
        findAppointmentButton.style.display = 'none';
        registerType = "registerBarber";
    }
    else {
        registerType = "registerClient";
    }

    const profileType = document.querySelector('input[name="profile-type"]:checked').value;
    const id = document.getElementById('id').value;
    const email = document.getElementById('email').value;
    const firstName = document.getElementById('firstname').value;
    const middleName = document.getElementById('middlename').value;
    const lastName = document.getElementById('lastname').value;
    const location = document.getElementById('location').value;

    const formData = {
        profileType,
        id,
        email,
        firstName,
        middleName,
        lastName,
        location,
    };

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors',
        body: JSON.stringify(formData)
    };

    fetch(`http://localhost:8081/${registerType}`, requestOptions)
        .then(response => response.json())
        .then(data => console.log(data))
        .catch(error => console.error(error))
});

viewBookedAppointmentsButton.addEventListener('click', (event) => {
    event.preventDefault();
    optionButtons.style.display = 'none';
    viewBookedAppointmentsPage.style.display = 'block';
});

addScheduleButton.addEventListener('click', (event) => {
    event.preventDefault();
    optionButtons.style.display = 'none';
    addSchedulePage.style.display = 'block';
});

nearbyBarbersButton.addEventListener('click', (event) => {
    event.preventDefault();
    optionButtons.style.display = 'none';
    nearbyBarbersPage.style.display = 'block';
});

allBarbersButton.addEventListener('click', (event) => {
    event.preventDefault();
    optionButtons.style.display = 'none';
    allBarbersPage.style.display = 'block';
});

bookAppointmentButton.addEventListener('click', (event) => {
    event.preventDefault();
    optionButtons.style.display = 'none';
    bookAppointmentPage.style.display = 'block';
});

cancelAppointmentButton.addEventListener('click', (event) => {
    event.preventDefault();
    optionButtons.style.display = 'none';
    cancelAppointmentPage.style.display = 'block';
});

findAppointmentButton.addEventListener('click', (event) => {
    event.preventDefault();
    optionButtons.style.display = 'none';
    findAppointmentPage.style.display = 'block';
});

