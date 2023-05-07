const bookBarberForm = document.getElementById('book-barber-form');
const backLink = document.getElementById('link');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);
backLink.href = 'clientOptions.html?id=' + id + '&zip=' + zip;

bookBarberForm.addEventListener('submit', (event) => {
    event.preventDefault();

    const formData = {
        barberId: bookBarberForm.elements['barber-id'].value,
        clientId: bookBarberForm.elements['client-id'].value,
        appointmentSlotId: bookBarberForm.elements['appointment-slot-id'].value,
    };

    console.log(formData);

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the 'Origin' header explicitly
        },
        mode: 'cors',
        body: JSON.stringify(formData)
    };

    fetch(`http://localhost:8081/newAppointment/${formData.barberId}/${bookBarberForm.elements['barber-zip'].value}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})