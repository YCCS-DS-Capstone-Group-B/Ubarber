const getBarberForm = document.getElementById('get-barber-form');

getBarberForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const getBarberPage = document.getElementById('get-barber-page');
    getBarberPage.style.display = 'none';
    const barberID = getBarberForm.elements['barber-id'].value;
    const barberZip = getBarberForm.elements['barber-zip'].value;

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

    fetch(`http://localhost:8081/getBarber/${barberID}/${barberZip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})