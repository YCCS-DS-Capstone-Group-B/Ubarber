const addScheduleForm = document.getElementById('add-schedule-form');

addScheduleForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const addSchedulePage = document.getElementById('add-schedule-page');
    addSchedulePage.style.display = 'none';
    const day = addScheduleForm.elements['day'].value;
    const startTime = addScheduleForm.elements['start-time'].value;
    const endTime = addScheduleForm.elements['end-time'].value;

    const appointment = {
        id: 13, // TODO: This needs to be generated somehow
        day: day,
        startTime: startTime,
        endTime: endTime
    };

    console.log(appointment);

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors',
        body: JSON.stringify(appointment)
    };

    const urlParams = new URLSearchParams(window.location.search);
    const zip = urlParams.get('zip');
    console.log(zip);
    const id = urlParams.get('id');
    console.log(id);

    fetch(`http://localhost:8081/updateSchedule/${id}/${zip}}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            const jsonElement = document.getElementById('json-data');
            jsonElement.innerHTML = JSON.stringify(data);
            jsonElement.style.display = 'block';
        })
        .catch(error => console.error(error))
})