const profileForm = document.getElementById('profile-form');

profileForm.addEventListener('submit', (event) => {
    event.preventDefault();
    registerType = "";
    if (document.getElementById('barber').checked) {
        registerType = "registerBarber";
    }
    else {
        registerType = "registerClient";
    }

    const profileType = document.querySelector('input[name="profile-type"]:checked').value;
    const id = document.getElementById('id').value;
    const email = document.getElementById('email').value;
    const firstName = document.getElementById('firstname').value;
    const middleName = document.getElementById('middlename').value;
    const lastName = document.getElementById('lastname').value;
    const location = document.getElementById('location').value;

    const formData = {
        profileType,
        id,
        email,
        firstName,
        middleName,
        lastName,
        location,
    };

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
        },
        mode: 'cors',
        body: JSON.stringify(formData)
    };

    fetch(`http://localhost:8081/${registerType}`, requestOptions)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if(registerType == "registerClient") {
                window.location.replace(`clientOptions.html?zip=${formData.location}&id=${formData.id}`);
            } else {
                window.location.replace(`barberOptions.html?zip=${formData.location}&id=${formData.id}`);
            }
        })
        .catch(error => console.error(error))
});