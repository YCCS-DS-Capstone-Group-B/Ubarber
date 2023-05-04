const deleteAppointmentSlotForm = document.getElementById('delete-appointment-slot-form');

deleteAppointmentSlotForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const deleteAppointmentSlotPage = document.getElementById('delete-appointment-slot-page');
    deleteAppointmentSlotPage.style.display = 'none';
    const appointmentSlotId = deleteAppointmentSlotForm.elements['appointment-slot-id'].value;
    const barberZip = deleteAppointmentSlotForm.elements['barber-zip'].value;

    console.log(appointmentSlotId);

    const requestOptions = {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors'
    };

    fetch(`http://localhost:8081/deleteAppointmentSlot/${appointmentSlotId}/${barberZip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})