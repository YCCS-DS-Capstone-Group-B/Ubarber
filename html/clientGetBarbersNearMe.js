const getBarbersNearMeForm = document.getElementById('get-barbers-near-me-form');

getBarbersNearMeForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const getBarbersNearMePage = document.getElementById('get-barbers-near-me-page');
    getBarbersNearMePage.style.display = 'none';
    const clientZip = getBarbersNearMeForm.elements['client-zip'].value;

    console.log(clientZip);

    const requestOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors'
    };

    fetch(`http://localhost:8081/getBarbersNearMe/${clientZip}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})