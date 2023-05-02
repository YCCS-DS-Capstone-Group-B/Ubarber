const bookBarberForm = document.getElementById("book-barber-form");

bookBarberForm.addEventListener('submit', (event) => {
    event.preventDefault();

    const formData = {
        appointmentId: bookBarberForm.elements["appointment-id"].value,
        barberId: bookBarberForm.elements["barber-id"].value,
        clientId: bookBarberForm.elements["client-id"].value,
        appointmentSlotId: bookBarberForm.elements["appointment-slot-id"].value,
    };

    console.log(formData);

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors',
        body: formData
    };

    fetch(`http://localhost:8081/bookBarber/${formData.barberId}/${bookBarberForm.elements["barber-zip"].value}}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})