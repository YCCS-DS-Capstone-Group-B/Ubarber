const getBarberScheduleForm = document.getElementById('get-barber-schedule-form');

getBarberScheduleForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const getBarberSchedulePage = document.getElementById('get-barber-schedule-page');
    getBarberSchedulePage.style.display = 'none';
    const barberID = getBarberScheduleForm.elements['barber-id'].value;
    const barberZip = getBarberScheduleForm.elements['barber-zip'].value;

    console.log(barberID);
    console.log(barberZip);

    const requestOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors'
    };

    fetch(`http://localhost:8081/barberSchedule/${barberID}/${barberZip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})