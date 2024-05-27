document.addEventListener('DOMContentLoaded', function() {
    const inputDivs = document.querySelectorAll('.inputDiv');

    inputDivs.forEach(function(div) {
        const checkbox = div.querySelector('input[type="checkbox"]');
        const hiddenDiv = div.querySelector('.hidden');

        checkbox.addEventListener('change', function() {
            if (this.checked) {
                hiddenDiv.classList.remove('hidden');
            } else {
                hiddenDiv.classList.add('hidden');
            }
        });
        if (!checkbox.checked) {
            hiddenDiv.classList.add('hidden');
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const startButton = document.getElementById('startButton');
    const inputDivs = document.querySelectorAll('.inputDiv');

    startButton.addEventListener('click', function() {
        inputDivs.forEach(function(div) {
            const checkbox = div.querySelector('input[type="checkbox"]');
            const canvasId = checkbox.id + "CanvasDiv";
            const canvasDiv = document.querySelector('#' + canvasId);

            if (checkbox.checked) {
                canvasDiv.style.display = "block";
            } else {
                canvasDiv.style.display = "none";
            }
        });
    });
});


function handleFileSelect(event) {
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById("fileContent").value = e.target.result;
    };
    reader.readAsText(file);
}





