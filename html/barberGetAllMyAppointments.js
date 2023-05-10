const barberGetAllMyAppointmentsForm = document.getElementById('barber-get-all-my-appointments-form');
const backLink = document.getElementById('link');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);
backLink.href = 'barberOptions.html?id=' + id + '&zip=' + zip;

barberGetAllMyAppointmentsForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const barberGetAllMyAppointmentsPage = document.getElementById('barber-get-all-my-appointments-page');
    barberGetAllMyAppointmentsPage.style.display = 'none';
    const barberID = barberGetAllMyAppointmentsForm.elements['barber-id'].value;

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

    fetch(`http://localhost:8081/barber/myAppointments/${barberID}/${zip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})