const updateProfileForm = document.getElementById('update-profile-form');
const backLink = document.getElementById('link');
const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);
backLink.href = 'clientOptions.html?id=' + id + '&zip=' + zip;

updateProfileForm.addEventListener('submit', (event) => {
  event.preventDefault();

  const updateProfilePage = document.getElementById('update-profile-page');
  updateProfilePage.style.display = 'none';

  const clientId = document.getElementById('id').value;
  const email = document.getElementById('email').value;
  const firstname = document.getElementById('firstname').value;
  const middlename = document.getElementById('middlename').value;
  const lastname = document.getElementById('lastname').value;
  const location = document.getElementById('location').value;

  const formData = {
    id: clientId,
    email: email,
    firstName: firstname,
    middleName: middlename,
    lastName: lastname,
    location: location
  };

  console.log(formData);

  const requestOptions = {
    method: 'PUT',
    headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        // 'Origin': 'http://127.0.0.1:5500' // set the "Origin" header explicitly
    },
    mode: 'cors',
    body: JSON.stringify(formData)
};

const urlParams = new URLSearchParams(window.location.search);
const zip = urlParams.get('zip');
console.log(zip);
const id = urlParams.get('id');
console.log(id);

fetch(`http://localhost:8081/clientUpdateProfile/${id}/${zip}`, requestOptions)
    .then(response => response.json())
    .then(data => {
        const jsonElement = document.getElementById('json-data');
        jsonElement.innerHTML = JSON.stringify(data);
        jsonElement.style.display = 'block';
    })
    .catch(error => console.error(error))
});
