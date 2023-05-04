const addAppointmentSlotForm = document.getElementById('add-appointment-slot-form');

addAppointmentSlotForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const addAppointmentSlotPage = document.getElementById('add-appointment-slot-page');
    addAppointmentSlotPage.style.display = 'none';
    const date = addAppointmentSlotForm.elements['date'].value;
    const startTime = addAppointmentSlotForm.elements['start-time'].value;
    const endTime = addAppointmentSlotForm.elements['end-time'].value;
    const urlParams = new URLSearchParams(window.location.search);
    const zip = urlParams.get('zip');
    console.log(zip);
    const id = urlParams.get('id');
    console.log(id);

    const appointmentSlot = {
        appointmentSlotId: 13, // TODO: This needs to be generated somehow
        barberId: id,
        date: date,
        startTime: startTime,
        endTime: endTime
    };

    console.log(appointmentSlot);

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors',
        body: JSON.stringify(appointmentSlot)
    };


    fetch(`http://localhost:8081/addAppointmentSlot/${zip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})