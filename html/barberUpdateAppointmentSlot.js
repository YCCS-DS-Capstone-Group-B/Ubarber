const updateAppointmentSlotForm = document.getElementById('update-appointment-slot-form');
const backLink = document.getElementById('link');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);
backLink.href = 'barberOptions.html?id=' + id + '&zip=' + zip;

updateAppointmentSlotForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const updateAppointmentSlotPage = document.getElementById('update-appointment-slot-page');
    updateAppointmentSlotPage.style.display = 'none';
    const appointmentSlotId = updateAppointmentSlotForm.elements['appointment-slot-id'].value;
    const date = updateAppointmentSlotForm.elements['date'].value;
    const startTime = updateAppointmentSlotForm.elements['start-time'].value;
    const endTime = updateAppointmentSlotForm.elements['end-time'].value;

    const appointmentSlot = {
        appointmentSlotId: appointmentSlotId,
        barberId: id,
        date: date,
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


    fetch(`http://localhost:8081/updateAppointmentSlot/${appointmentSlot.appointmentSlotId}/${zip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})