const deleteAppointmentSlotForm = document.getElementById('delete-appointment-slot-form');
const backLink = document.getElementById('link');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);
backLink.href = 'barberOptions.html?id=' + id + '&zip=' + zip;

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

    fetch(`http://service-env-1.us-east-2.elasticbeanstalk.com/deleteAppointmentSlot/${appointmentSlotId}/${barberZip}`, requestOptions)
        .then(response => {
            if (response.status === 204) {
                return Promise.resolve();
            } else {
                return response.json();
            }
        })
        .then(data => {
            if (data) {
                const jsonElement = document.getElementById('json-data');
                jsonElement.innerHTML = JSON.stringify(data);
                jsonElement.style.display = 'block';
            }
        })
        .catch(error => console.error(error))

})