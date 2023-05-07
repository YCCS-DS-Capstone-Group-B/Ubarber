const barberGetAllMyAppointmentSlotsForm = document.getElementById('barber-get-all-my-appointment-slots-form');
const backLink = document.getElementById('link');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);
backLink.href = 'barberOptions.html?id=' + id + '&zip=' + zip;

barberGetAllMyAppointmentSlotsForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const barberGetAllMyAppointmentSlotsPage = document.getElementById('barber-get-all-my-appointment-slots-page');
    barberGetAllMyAppointmentSlotsPage.style.display = 'none';
    const barberID = barberGetAllMyAppointmentSlotsForm.elements['barber-id'].value;

    console.log(barberID);

    const requestOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors'
    };

    fetch(`http://localhost:8081/barber/myAppointmentSlots/${barberID}/${zip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})