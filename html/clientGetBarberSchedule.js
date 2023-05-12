const getBarberScheduleForm = document.getElementById('get-barber-schedule-form');
const backLink = document.getElementById('link');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);
backLink.href = 'clientOptions.html?id=' + id + '&zip=' + zip;

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

    fetch(`http://service-env-1.us-east-2.elasticbeanstalk.com/barberSchedule/${barberID}/${barberZip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})