const updateAppointmentSlotForm = document.getElementById('update-appointment-slot-form');

updateAppointmentSlotForm.updateEventListener('submit', (event) => {
    event.preventDefault();
    const updateAppointmentSlotPage = document.getElementById('update-appointment-slot-page');
    updateAppointmentSlotPage.style.display = 'none';
    const day = updateAppointmentSlotForm.elements['day'].value;
    const startTime = updateAppointmentSlotForm.elements['start-time'].value;
    const endTime = updateAppointmentSlotForm.elements['end-time'].value;
    const urlParams = new URLSearchParams(window.location.search);
    const zip = urlParams.get('zip');
    console.log(zip);
    const id = urlParams.get('id');
    console.log(id);

    const appointmentSlot = {
        appointmentSlotId: 13, // TODO: This needs to be generated somehow
        barberId: id,
        day: day,
        startTime: startTime,
        endTime: endTime
    };

    console.log(appointmentSlot);

    const requestOptions = {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors',
        body: JSON.stringify(appointmentSlot)
    };


    fetch(`http://localhost:8081/updateAppointmentSlot/${appointmentSlotId}/${zip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})