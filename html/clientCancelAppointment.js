const cancelAppointmentForm = document.getElementById('cancel-appointment-form');

cancelAppointmentForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const cancelAppointmentPage = document.getElementById('cancel-appointment-page');
    cancelAppointmentPage.style.display = 'none';
    const appointmentID = cancelAppointmentForm.elements['appointment-id'].value;
    const barberZip = cancelAppointmentForm.elements['barber-zip'].value;

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

    fetch(`http://localhost:8081/clientCancelAppointment/${appointmentID}/${barberZip}}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})