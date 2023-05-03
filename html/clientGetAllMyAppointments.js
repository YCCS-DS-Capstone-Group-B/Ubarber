const clientGetAllMyAppointmentsForm = document.getElementById('client-get-all-my-appointments-form');

clientGetAllMyAppointmentsForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const clientGetAllMyAppointmentsPage = document.getElementById('client-get-all-my-appointments-page');
    clientGetAllMyAppointmentsPage.style.display = 'none';
    const clientID = clientGetAllMyAppointmentsForm.elements['client-id'].value;
    const urlParams = new URLSearchParams(window.location.search);
    const zip = urlParams.get('zip');

    console.log(clientID);

    const requestOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors'
    };

    fetch(`http://localhost:8081/client/myAppointments/${clientID}/${zip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})