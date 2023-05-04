const cancelAppointmentForm = document.getElementById('cancel-appointment-form');

cancelAppointmentForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const cancelAppointmentPage = document.getElementById('cancel-appointment-page');
    cancelAppointmentPage.style.display = 'none';
    const appointmentID = cancelAppointmentForm.elements['appointment-id'].value;

    console.log(appointmentID);

    const requestOptions = {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors'
    };

    const urlParams = new URLSearchParams(window.location.search);
    const zip = urlParams.get('zip');
    console.log(zip);

    fetch(`http://localhost:8081/barberCancelAppointment/${appointmentID}/${zip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})